package com.rabiausul.crisismanagementapp.victim

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.rabiausul.crisismanagementapp.model.Assignment

@Composable
fun RequestStatusScreen(
    request: AidRequest,
    onBack: () -> Unit
) {
    var assignments by remember { mutableStateOf<List<Assignment>>(emptyList()) }
    var isLoadingAssignments by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.api.getAssignmentsByRequest(request.requestId)
            if (response.isSuccessful) {
                assignments = response.body() ?: emptyList()
            }
        } catch (e: Exception) {
            // Atama bilgisi yüklenemezse sessizce geç
        } finally {
            isLoadingAssignments = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Başlık
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
                text = "Talep Detayı",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Talep Bilgileri Kartı
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Talep Bilgileri",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                DetailRow(label = "Kategori", value = request.category)
                DetailRow(label = "Açıklama", value = request.description)
                DetailRow(label = "Etkilenen Kişi", value = "${request.vulnerableCount} kişi")
                DetailRow(
                    label = "Aciliyet",
                    value = when (request.urgencyLevel) {
                        3 -> "Yüksek"
                        2 -> "Orta"
                        else -> "Düşük"
                    },
                    valueColor = when (request.urgencyLevel) {
                        3 -> Color(0xFFE53935)
                        2 -> Color(0xFFFB8C00)
                        else -> Color(0xFF43A047)
                    }
                )
                if (request.times != null) {
                    DetailRow(label = "Oluşturulma", value = request.times.take(16).replace("T", " "))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Durum Timeline Kartı
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Talep Durumu",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                StatusTimeline(currentStatus = request.status)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Atama Bilgileri Kartı
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Atama Bilgileri",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (isLoadingAssignments) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else if (assignments.isEmpty()) {
                    Text(
                        text = "Henüz atama yapılmamış",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                } else {
                    assignments.forEach { assignment ->
                        AssignmentItem(assignment = assignment)
                        if (assignment != assignments.last()) {
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }
            }
        }
    }
}

// Detay satırı bileşeni
@Composable
fun DetailRow(
    label: String,
    value: String,
    valueColor: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = valueColor,
            modifier = Modifier.weight(2f)
        )
    }
}

// Durum timeline bileşeni
@Composable
fun StatusTimeline(currentStatus: String) {
    val steps = listOf(
        "PENDING" to "Talep Oluşturuldu",
        "APPROVED" to "Onaylandı",
        "COMPLETED" to "Tamamlandı"
    )

    // Reddedildiyse farklı göster
    if (currentStatus == "REJECTED") {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(Color(0xFFE53935), CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Talep Reddedildi",
                color = Color(0xFFE53935),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }
        return
    }

    val currentIndex = steps.indexOfFirst { it.first == currentStatus }

    steps.forEachIndexed { index, (_, label) ->
        val isCompleted = index <= currentIndex
        val isActive = index == currentIndex

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Nokta
            Box(
                modifier = Modifier
                    .size(if (isActive) 20.dp else 14.dp)
                    .background(
                        when {
                            isActive -> Color(0xFF1E88E5)
                            isCompleted -> Color(0xFF43A047)
                            else -> Color.LightGray
                        },
                        CircleShape
                    )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                color = when {
                    isActive -> Color(0xFF1E88E5)
                    isCompleted -> Color(0xFF43A047)
                    else -> Color.Gray
                }
            )
        }

        // Bağlantı çizgisi (son eleman değilse)
        if (index < steps.size - 1) {
            Box(
                modifier = Modifier
                    .padding(start = 6.dp)
                    .width(2.dp)
                    .height(24.dp)
                    .background(if (index < currentIndex) Color(0xFF43A047) else Color.LightGray)
            )
        }
    }
}

// Atama kartı bileşeni
@Composable
fun AssignmentItem(assignment: Assignment) {
    val statusText = when (assignment.status) {
        "ASSIGNED" -> "Atandı"
        "IN_PROGRESS" -> "Devam Ediyor"
        "COMPLETED" -> "Tamamlandı"
        else -> assignment.status
    }
    val statusColor = when (assignment.status) {
        "ASSIGNED" -> Color(0xFFFB8C00)
        "IN_PROGRESS" -> Color(0xFF1E88E5)
        "COMPLETED" -> Color(0xFF43A047)
        else -> Color.Gray
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Atama #${assignment.assignmentId}",
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
            Text(
                text = "Miktar: ${assignment.quantity}",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
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
}