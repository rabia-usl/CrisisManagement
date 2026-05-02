package com.rabiausul.crisismanagementapp.volunteer

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
import kotlinx.coroutines.launch

@Composable
fun VolunteerTasksScreen(onBack: () -> Unit) {
    var tasks by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    fun loadTasks() {
        scope.launch {
            isLoading = true
            errorMessage = ""
            try {
                val response = RetrofitClient.api.getAssignmentsByVolunteer(SessionManager.getUserId())
                if (response.isSuccessful) {
                    tasks = response.body() ?: emptyList()
                } else {
                    errorMessage = "Görevler yüklenemedi"
                }
            } catch (e: Exception) {
                errorMessage = "Bağlantı hatası: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) { loadTasks() }

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
                Text(text = "Görevlerim", fontSize = 24.sp, fontWeight = FontWeight.Bold)
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
                tasks.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Henüz göreviniz bulunmuyor")
                    }
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(tasks) { task ->
                            TaskCard(
                                task = task,
                                onStatusUpdate = { assignmentId, status ->
                                    scope.launch {
                                        try {
                                            val response = RetrofitClient.api.updateAssignmentStatus(assignmentId, status)
                                            if (response.isSuccessful) {
                                                snackbarHostState.showSnackbar("Durum güncellendi")
                                                loadTasks()
                                            } else {
                                                snackbarHostState.showSnackbar("Güncelleme başarısız")
                                            }
                                        } catch (e: Exception) {
                                            snackbarHostState.showSnackbar("Bağlantı hatası: ${e.message}")
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    task: Map<String, Any>,
    onStatusUpdate: (Int, String) -> Unit
) {
    val assignmentId = when (val v = task["assignmentid"]) {
        is Double -> v.toInt()
        is Number -> v.toInt()
        else -> 0
    }
    val status = task["status"]?.toString() ?: ""
    val category = task["category"]?.toString() ?: "-"
    val description = task["description"]?.toString() ?: "-"
    val urgencyLevel = when (val v = task["urgencylevel"]) {
        is Double -> v.toInt()
        is Number -> v.toInt()
        else -> 1
    }
    val vulnerableCount = when (val v = task["vulnerablecount"]) {
        is Double -> v.toInt()
        is Number -> v.toInt()
        else -> 0
    }
    val quantity = when (val v = task["quantity"]) {
        is Double -> v.toInt()
        is Number -> v.toInt()
        else -> 0
    }

    val urgencyColor = when (urgencyLevel) {
        3 -> Color(0xFFE53935)
        2 -> Color(0xFFFB8C00)
        else -> Color(0xFF43A047)
    }
    val urgencyText = when (urgencyLevel) {
        3 -> "Yüksek"
        2 -> "Orta"
        else -> "Düşük"
    }
    val statusColor = when (status) {
        "PENDING" -> Color(0xFFFB8C00)
        "IN_PROGRESS" -> Color(0xFF1E88E5)
        "COMPLETED" -> Color(0xFF43A047)
        "CANCELLED" -> Color(0xFFE53935)
        else -> Color.Gray
    }
    val statusText = when (status) {
        "PENDING" -> "Bekliyor"
        "IN_PROGRESS" -> "Devam Ediyor"
        "COMPLETED" -> "Tamamlandı"
        "CANCELLED" -> "İptal Edildi"
        else -> status
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = category, fontWeight = FontWeight.Bold, fontSize = 18.sp)
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

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = description, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = urgencyColor.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Aciliyet: $urgencyText",
                        color = urgencyColor,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Text(text = "Etkilenen: $vulnerableCount kişi", fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Görev #$assignmentId  •  Miktar: $quantity", fontSize = 12.sp, color = Color.Gray)

            when (status) {
                "PENDING" -> {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { onStatusUpdate(assignmentId, "IN_PROGRESS") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                    ) {
                        Text("Göreve Başla", color = Color.White)
                    }
                }
                "IN_PROGRESS" -> {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { onStatusUpdate(assignmentId, "COMPLETED") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
                    ) {
                        Text("Tamamlandı", color = Color.White)
                    }
                }
            }
        }
    }
}