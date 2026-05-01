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
import com.rabiausul.crisismanagementapp.model.RequestResourceMatch
import com.rabiausul.crisismanagementapp.model.Resource

@Composable
fun MatchScreen(onBack: () -> Unit) {
    var requests by remember { mutableStateOf<List<AidRequest>>(emptyList()) }
    var resources by remember { mutableStateOf<List<Resource>>(emptyList()) }
    var matches by remember { mutableStateOf<List<RequestResourceMatch>>(emptyList()) }
    var selectedRequest by remember { mutableStateOf<AidRequest?>(null) }
    var selectedResource by remember { mutableStateOf<Resource?>(null) }
    var quantity by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val requestResponse = RetrofitClient.api.getAllRequests()
            val resourceResponse = RetrofitClient.api.getAllResources()
            val matchResponse = RetrofitClient.api.getAllMatches()

            if (requestResponse.isSuccessful) requests = requestResponse.body() ?: emptyList()
            if (resourceResponse.isSuccessful) resources = resourceResponse.body() ?: emptyList()
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
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Geri"
                )
            }
            Text(
                text = "Eşleştirme Yönetimi",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Column
        }

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
            return@Column
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Yeni Eşleştirme",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(text = "Talep Seç:", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                requests.forEach { request ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedRequest == request,
                            onClick = { selectedRequest = request }
                        )
                        Text(text = "${request.category} - Aciliyet: ${request.urgencyLevel}")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Kaynak Seç:", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                resources.forEach { resource ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedResource == resource,
                            onClick = { selectedResource = resource }
                        )
                        Text(text = "${resource.category} - Mevcut: ${resource.currentQuantity}")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Miktar") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (successMessage.isNotEmpty()) {
                    Text(text = successMessage, color = Color(0xFF43A047))
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        if (selectedRequest != null && selectedResource != null && quantity.isNotEmpty()) {
                            val match = RequestResourceMatch(
                                requestId = selectedRequest!!.requestId,
                                resourceId = selectedResource!!.resourceId,
                                allocateQuantity = quantity.toIntOrNull() ?: 0
                            )
                            successMessage = "Eşleştirme başarıyla oluşturuldu"
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
                ) {
                    Text("Eşleştir", color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Mevcut Eşleştirmeler",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (matches.isEmpty()) {
            Text(text = "Henüz eşleştirme bulunmuyor", color = Color.Gray)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(matches) { match ->
                    MatchCard(match = match)
                }
            }
        }
    }
}