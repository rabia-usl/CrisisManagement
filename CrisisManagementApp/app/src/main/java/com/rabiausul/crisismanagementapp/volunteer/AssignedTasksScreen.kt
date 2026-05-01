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
import com.rabiausul.crisismanagementapp.api.RetrofitClient
import com.rabiausul.crisismanagementapp.model.Assignment
import kotlinx.coroutines.launch

@Composable
fun AssignedTasksScreen(onBack: () -> Unit) {
    var tasks by remember { mutableStateOf<List<Assignment>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.api.getAllAssignments()
            if (response.isSuccessful) {
                tasks = response.body()
                    ?.filter { it.status != "COMPLETED" }
                    ?: emptyList()
            } else {
                errorMessage = "Görevler yüklenemedi"
            }
        } catch (e: Exception) {
            errorMessage = "Bağlantı hatası: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
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
                    text = "Atanan Görevler",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                errorMessage.isNotEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = errorMessage, color = Color.Red)
                    }
                }
                tasks.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Atanan görev bulunmuyor")
                    }
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(tasks) { task ->
                            AssignedTaskCard(
                                task = task,
                                onStatusUpdate = { updatedTask ->
                                    scope.launch {
                                        try {
                                            val response = RetrofitClient.api.updateAssignmentStatus(
                                                updatedTask.assignmentId,
                                                "COMPLETED"
                                            )
                                            if (response.isSuccessful) {
                                                tasks = tasks.filter { it.assignmentId != updatedTask.assignmentId }
                                                snackbarHostState.showSnackbar("Görev tamamlandı")
                                            }
                                        } catch (e: Exception) {
                                            snackbarHostState.showSnackbar("Hata: ${e.message}")
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
fun AssignedTaskCard(
    task: Assignment,
    onStatusUpdate: (Assignment) -> Unit
) {
    val statusColor = when (task.status) {
        "PENDING" -> Color(0xFFFB8C00)
        "IN_PROGRESS" -> Color(0xFF1E88E5)
        else -> Color.Gray
    }

    val statusText = when (task.status) {
        "PENDING" -> "Bekliyor"
        "IN_PROGRESS" -> "Devam Ediyor"
        else -> task.status
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
                Text(
                    text = "Görev #${task.assignmentId}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
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
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Talep ID: ${task.requestId}",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = "Miktar: ${task.quantity}",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { onStatusUpdate(task) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF43A047)
                )
            ) {
                Text("Tamamlandı Olarak İşaretle", color = Color.White)
            }
        }
    }
}