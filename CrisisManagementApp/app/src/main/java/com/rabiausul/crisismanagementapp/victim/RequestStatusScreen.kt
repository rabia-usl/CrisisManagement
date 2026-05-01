package com.rabiausul.crisismanagementapp.victim

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rabiausul.crisismanagementapp.api.RetrofitClient
import com.rabiausul.crisismanagementapp.model.AidRequest
import com.rabiausul.crisismanagementapp.model.RequestResourceMatch

@Composable
fun RequestStatusScreen(onBack: () -> Unit) {
    var requests by remember { mutableStateOf<List<AidRequest>>(emptyList()) }
    var matches by remember { mutableStateOf<List<RequestResourceMatch>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val requestResponse = RetrofitClient.api.getAllRequests()
            val matchResponse = RetrofitClient.api.getAllMatches()
            if (requestResponse.isSuccessful) requests = requestResponse.body() ?: emptyList()
            if (matchResponse.isSuccessful) matches = matchResponse.body() ?: emptyList()
        } catch (e: Exception) {
            errorMessage = "Bağlantı hatası: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Geri"
                )
            }
            Text(
                text = "Talep Durumu",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            errorMessage.isNotEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = errorMessage, color = Color.Red)
                }
            }
            requests.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Henüz talebiniz bulunmuyor")
                }
            }
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(requests) { request ->
                        val requestMatches = matches.filter { it.requestId == request.requestId }
                        RequestStatusCard(
                            request = request,
                            matches = requestMatches
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RequestStatusCard(
    request: AidRequest,
    matches: List<RequestResourceMatch>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = request.category,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = request.description, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            // Durum Göstergesi
            val statusColor = when (request.status) {
                "PENDING" -> Color(0xFFFB8C00)
                "APPROVED" -> Color(0xFF43A047)
                "REJECTED" -> Color(0xFFE53935)
                "COMPLETED" -> Color(0xFF1E88E5)
                else -> Color.Gray
            }
            val statusText = when (request.status) {
                "PENDING" -> "Bekliyor"
                "APPROVED" -> "Onaylandı"
                "REJECTED" -> "Reddedildi"
                "COMPLETED" -> "Tamamlandı"
                else -> request.status
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Durum: ", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Card(
                    colors = CardDefaults.cardColors(containerColor = statusColor),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = statusText,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp
                    )
                }
            }

            // Eşleştirmeler
            if (matches.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Atanan Kaynaklar:",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                matches.forEach { match ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Kaynak #${match.resourceId}",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "Miktar: ${match.allocateQuantity}",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Henüz kaynak atanmadı",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
    }
}