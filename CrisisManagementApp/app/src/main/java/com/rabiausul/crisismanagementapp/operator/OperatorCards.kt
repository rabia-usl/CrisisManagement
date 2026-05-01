package com.rabiausul.crisismanagementapp.operator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rabiausul.crisismanagementapp.model.AidRequest
import com.rabiausul.crisismanagementapp.model.RequestResourceMatch
import com.rabiausul.crisismanagementapp.model.Resource

@Composable
fun RequestCard(request: AidRequest) {
    val urgencyColor = when (request.urgencyLevel) {
        3 -> Color(0xFFE53935)
        2 -> Color(0xFFFB8C00)
        else -> Color(0xFF43A047)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
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
            Text(text = "Durum: ${request.status}", fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ResourceCard(resource: Resource) {
    val quantityColor = when {
        resource.currentQuantity == 0 -> Color(0xFFE53935)
        resource.currentQuantity < resource.initialQuantity / 2 -> Color(0xFFFB8C00)
        else -> Color(0xFF43A047)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = resource.category,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Card(
                    colors = CardDefaults.cardColors(containerColor = quantityColor),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Miktar: ${resource.currentQuantity}",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Başlangıç Miktarı: ${resource.initialQuantity}", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Sağlayıcı ID: ${resource.providerId}", fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun PendingRequestCard(request: AidRequest) {
    val urgencyColor = when (request.urgencyLevel) {
        3 -> Color(0xFFE53935)
        2 -> Color(0xFFFB8C00)
        else -> Color(0xFF43A047)
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
            Text(text = "Etkilenen Kişi Sayısı: ${request.vulnerableCount}", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { /* TODO: Onayla */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
                ) {
                    Text("Onayla", color = Color.White)
                }
                Button(
                    onClick = { /* TODO: Reddet */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) {
                    Text("Reddet", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun MatchCard(match: RequestResourceMatch) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Talep #${match.requestId}", fontWeight = FontWeight.Bold)
                Text(text = "Kaynak #${match.resourceId}", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Tahsis Edilen Miktar: ${match.allocateQuantity}", color = Color.Gray, fontSize = 14.sp)
            Text(text = "Tarih: ${match.matchDate}", color = Color.Gray, fontSize = 12.sp)
        }
    }
}