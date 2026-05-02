package com.rabiausul.crisismanagementapp.operator

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
import com.rabiausul.crisismanagementapp.model.RequestResourceMatch
import com.rabiausul.crisismanagementapp.model.Resource
import kotlinx.coroutines.launch

@Composable
fun RequestListScreen(onBack: () -> Unit) {
    var requests by remember { mutableStateOf<List<AidRequest>>(emptyList()) }
    var resources by remember { mutableStateOf<List<Resource>>(emptyList()) }
    var selectedRequest by remember { mutableStateOf<AidRequest?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        try {
            val lat = SessionManager.getLat()
            val lng = SessionManager.getLng()

            val requestResponse = RetrofitClient.api.getNearbyRequests(lat, lng)
            val resourceResponse = RetrofitClient.api.getNearbyResources(lat, lng)

            if (requestResponse.isSuccessful) {
                requests = requestResponse.body()
                    ?.map { map ->
                        AidRequest(
                            requestId = (map["requestid"] as? Number)?.toInt() ?: 0,
                            category = map["category"]?.toString() ?: "",
                            urgencyLevel = (map["urgencylevel"] as? Number)?.toInt() ?: 1,
                            description = map["description"]?.toString() ?: "",
                            status = map["status"]?.toString() ?: "",
                            vulnerableCount = (map["vulnerablecount"] as? Number)?.toInt() ?: 0
                        )
                    }
                    ?.filter { it.status == "Pending" || it.status == "Approved" }
                    ?.sortedByDescending { it.urgencyLevel }
                    ?: emptyList()
            }

            if (resourceResponse.isSuccessful) {
                resources = resourceResponse.body()
                    ?.map { map ->
                        Resource(
                            resourceId = (map["resourceid"] as? Number)?.toInt() ?: 0,
                            category = map["category"]?.toString() ?: "",
                            currentQuantity = (map["currentquantity"] as? Number)?.toInt() ?: 0,
                            initialQuantity = (map["initialquantity"] as? Number)?.toInt() ?: 0,
                            providerId = 0
                        )
                    } ?: emptyList()
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
                IconButton(onClick = if (selectedRequest != null) {
                    { selectedRequest = null }
                } else {
                    onBack
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Geri"
                    )
                }
                Text(
                    text = if (selectedRequest != null) "Kaynak Seç" else "Yardım Talepleri",
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
                selectedRequest == null -> {
                    if (requests.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Yakınınızda talep bulunmuyor")
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(requests) { request ->
                                val urgencyColor = when (request.urgencyLevel) {
                                    3 -> Color(0xFFE53935)
                                    2 -> Color(0xFFFB8C00)
                                    else -> Color(0xFF43A047)
                                }
                                val urgencyText = when (request.urgencyLevel) {
                                    3 -> "Yüksek"
                                    2 -> "Orta"
                                    else -> "Düşük"
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
                                                colors = CardDefaults.cardColors(
                                                    containerColor = urgencyColor
                                                ),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = urgencyText,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(
                                                        horizontal = 8.dp,
                                                        vertical = 4.dp
                                                    ),
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = request.description,
                                            fontSize = 14.sp,
                                            color = Color.Gray
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = "Etkilenen: ${request.vulnerableCount} kişi",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Button(
                                            onClick = { selectedRequest = request },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF1E88E5)
                                            )
                                        ) {
                                            Text("Kaynak Eşleştir", color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else -> {
                    val request = selectedRequest!!

                    // Seçilen talep özeti
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1E88E5)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = request.category,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Aciliyet: ${request.urgencyLevel} | Etkilenen: ${request.vulnerableCount} kişi",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val matchingResources = resources.filter {
                        it.category.equals(request.category, ignoreCase = true) &&
                                it.currentQuantity > 0
                    }

                    if (matchingResources.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFE53935)
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Bu kategori için uygun kaynak bulunamadı",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Uygun Kaynaklar",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(matchingResources) { resource ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = resource.category,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                            Text(
                                                text = "Mevcut: ${resource.currentQuantity}",
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                        }
                                        Button(
                                            onClick = {
                                                scope.launch {
                                                    try {
                                                        val allocate = minOf(
                                                            resource.currentQuantity,
                                                            request.vulnerableCount
                                                        )
                                                        val match = RequestResourceMatch(
                                                            requestId = request.requestId,
                                                            resourceId = resource.resourceId,
                                                            allocateQuantity = allocate
                                                        )
                                                        val matchResponse = RetrofitClient.api.createMatch(match)

                                                        if (matchResponse.isSuccessful) {
                                                            val newQuantity = resource.currentQuantity - allocate
                                                            RetrofitClient.api.updateResourceQuantity(
                                                                resource.resourceId,
                                                                newQuantity
                                                            )
                                                            RetrofitClient.api.updateRequestStatus(
                                                                request.requestId,
                                                                "Completed"
                                                            )
                                                            requests = requests.filter {
                                                                it.requestId != request.requestId
                                                            }
                                                            resources = resources.map {
                                                                if (it.resourceId == resource.resourceId) {
                                                                    it.copy(currentQuantity = newQuantity)
                                                                } else it
                                                            }
                                                            selectedRequest = null
                                                            snackbarHostState.showSnackbar(
                                                                "Eşleştirme başarıyla tamamlandı ✅"
                                                            )
                                                        }
                                                    } catch (e: Exception) {
                                                        snackbarHostState.showSnackbar(
                                                            "Hata: ${e.message}"
                                                        )
                                                    }
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF43A047)
                                            )
                                        ) {
                                            Text("Eşleştir", color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}