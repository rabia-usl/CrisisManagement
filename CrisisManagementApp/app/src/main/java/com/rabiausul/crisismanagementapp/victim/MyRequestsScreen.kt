package com.rabiausul.crisismanagementapp.victim

import androidx.compose.foundation.clickable
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
import com.rabiausul.crisismanagementapp.SessionManager
import com.rabiausul.crisismanagementapp.api.RetrofitClient
import com.rabiausul.crisismanagementapp.model.AidRequest
import kotlinx.coroutines.launch

@Composable
fun MyRequestsScreen(onBack: () -> Unit) {
    var requests by remember { mutableStateOf<List<AidRequest>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var requestToDelete by remember { mutableStateOf<AidRequest?>(null) }
    var selectedRequest by remember { mutableStateOf<AidRequest?>(null) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Detay ekranına geçildiyse onu göster
    if (selectedRequest != null) {
        RequestStatusScreen(
            request = selectedRequest!!,
            onBack = { selectedRequest = null }
        )
        return
    }

    fun loadRequests() {
        scope.launch {
            isLoading = true
            errorMessage = ""
            try {
                val response = RetrofitClient.api.getRequestsByVictim(SessionManager.getUserId())
                if (response.isSuccessful) {
                    requests = response.body() ?: emptyList()
                } else {
                    errorMessage = "Talepler yüklenemedi"
                }
            } catch (e: Exception) {
                errorMessage = "Bağlantı hatası: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) { loadRequests() }

    // Silme onay dialog'u
    if (requestToDelete != null) {
        AlertDialog(
            onDismissRequest = { requestToDelete = null },
            title = { Text("Talebi İptal Et") },
            text = { Text("Bu talebi iptal etmek istediğinizden emin misiniz?") },
            confirmButton = {
                Button(
                    onClick = {
                        val id = requestToDelete!!.requestId
                        requestToDelete = null
                        scope.launch {
                            try {
                                val response = RetrofitClient.api.deleteRequest(id)
                                if (response.isSuccessful) {
                                    snackbarHostState.showSnackbar("Talep iptal edildi")
                                    loadRequests()
                                } else {
                                    snackbarHostState.showSnackbar("İptal işlemi başarısız")
                                }
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Bağlantı hatası: ${e.message}")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) {
                    Text("İptal Et")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { requestToDelete = null }) {
                    Text("Vazgeç")
                }
            }
        )
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                    text = "Taleplerim",
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
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(requests) { request ->
                            MyRequestCard(
                                request = request,
                                onCardClick = { selectedRequest = request },
                                onDeleteClick = { requestToDelete = request }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyRequestCard(
    request: AidRequest,
    onCardClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val urgencyColor = when (request.urgencyLevel) {
        3 -> Color(0xFFE53935)
        2 -> Color(0xFFFB8C00)
        else -> Color(0xFF43A047)
    }

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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = request.category,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Card(
                    colors = CardDefaults.cardColors(containerColor = urgencyColor),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Aciliyet: ${request.urgencyLevel}",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = request.description, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Etkilenen: ${request.vulnerableCount} kişi",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
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

            if (request.status == "PENDING") {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE53935))
                ) {
                    Text("İptal Et")
                }
            }
        }
    }
}