package com.rabiausul.crisismanagementapp.operator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

@Composable
fun PendingRequestsScreen(onBack: () -> Unit) {
    var pendingRequests by remember { mutableStateOf<List<AidRequest>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.api.getAllRequests()
            if (response.isSuccessful) {
                pendingRequests = response.body()
                    ?.filter { it.status == "PENDING" }
                    ?: emptyList()
            } else {
                errorMessage = "Veriler yüklenemedi"
            }
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
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Geri"
                )
            }
            Text(
                text = "Bekleyen Talepler",
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
            pendingRequests.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Bekleyen talep bulunmuyor")
                }
            }
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(pendingRequests) { request ->
                        PendingRequestCard(request = request)
                    }
                }
            }
        }
    }
}