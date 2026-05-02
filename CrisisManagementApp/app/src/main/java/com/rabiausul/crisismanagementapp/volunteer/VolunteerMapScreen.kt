package com.rabiausul.crisismanagementapp.volunteer

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.rabiausul.crisismanagementapp.operator.MarkerUtils
import com.rabiausul.crisismanagementapp.SessionManager
import com.rabiausul.crisismanagementapp.api.RetrofitClient
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VolunteerMapScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var isLoading by remember { mutableStateOf(true) }
    var requests by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var selectedRequest by remember { mutableStateOf<Map<String, Any>?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    val defaultLat = 39.9334
    val defaultLng = 32.8597

    LaunchedEffect(Unit) {
        try {
            val requestResponse = RetrofitClient.api.getNearbyRequests(defaultLat, defaultLng)
            if (requestResponse.isSuccessful) requests = requestResponse.body() ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    // Talep detay bottom sheet
    if (showBottomSheet && selectedRequest != null) {
        val request = selectedRequest!!
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false }
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = request["category"]?.toString() ?: "Talep",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                RequestDetailRow("Açıklama", request["description"]?.toString() ?: "-")
                RequestDetailRow("Aciliyet", when(request["urgencylevel"]?.toString()) {
                    "3" -> "Yüksek"
                    "2" -> "Orta"
                    else -> "Düşük"
                })
                RequestDetailRow("Etkilenen Kişi", "${request["vulnerablecount"]} kişi")
                RequestDetailRow("Durum", request["status"]?.toString() ?: "-")

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val requestId = when (val v = request["requestid"]) {
                            is Double -> v.toInt()
                            is Number -> v.toInt()
                            else -> return@Button
                        }
                        scope.launch {
                            try {
                                // Yeni atama oluştur
                                val assignment = com.rabiausul.crisismanagementapp.model.Assignment(
                                    requestId = requestId,
                                    volunteerId = SessionManager.getUserId(),
                                    quantity = 1,
                                    status = "IN_PROGRESS"
                                )
                                val response = RetrofitClient.api.createAssignment(assignment)
                                if (response.isSuccessful) {
                                    showBottomSheet = false
                                    snackbarHostState.showSnackbar("Görev başarıyla üstlenildi")
                                } else {
                                    snackbarHostState.showSnackbar("Görev üstlenilemedi: ${response.code()}")
                                }
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Bağlantı hatası: ${e.message}")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                ) {
                    Text("Görevi Üstlen", color = Color.White)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Geri"
                    )
                }
                Text(
                    text = "Yakınımdaki Talepler",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                VolunteerMapLegendItem(color = Color.Red, label = "🔴 Yardım Talepleri")
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                AndroidView(
                    factory = { ctx ->
                        Configuration.getInstance().userAgentValue = ctx.packageName
                        MapView(ctx).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            controller.setZoom(12.0)
                            controller.setCenter(GeoPoint(defaultLat, defaultLng))

                            val redMarker = MarkerUtils.createColoredMarker(ctx, AndroidColor.RED)
                            requests.forEach { request ->
                                try {
                                    val lat = when (val v = request["lat"]) {
                                        is Double -> v
                                        is Number -> v.toDouble()
                                        else -> return@forEach
                                    }
                                    val lng = when (val v = request["lng"]) {
                                        is Double -> v
                                        is Number -> v.toDouble()
                                        else -> return@forEach
                                    }
                                    val marker = Marker(this)
                                    marker.position = GeoPoint(lat, lng)
                                    marker.icon = redMarker
                                    marker.title = request["category"]?.toString() ?: "Talep"
                                    marker.snippet = "Aciliyet: ${request["urgencylevel"]} | Durum: ${request["status"]}"
                                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)

                                    // Marker'a tıklanınca bottom sheet aç
                                    marker.setOnMarkerClickListener { _, _ ->
                                        selectedRequest = request
                                        showBottomSheet = true
                                        true
                                    }

                                    overlays.add(marker)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun RequestDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(text = value, fontWeight = FontWeight.Medium, fontSize = 14.sp, modifier = Modifier.weight(2f))
    }
}

@Composable
fun VolunteerMapLegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            modifier = Modifier.size(12.dp),
            shape = CircleShape,
            color = color
        ) {}
        Text(text = label, fontSize = 12.sp)
    }
}