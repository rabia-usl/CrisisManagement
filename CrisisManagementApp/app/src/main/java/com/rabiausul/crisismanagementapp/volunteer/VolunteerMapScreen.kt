package com.rabiausul.crisismanagementapp.volunteer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.rabiausul.crisismanagementapp.api.RetrofitClient
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun VolunteerMapScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var requests by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

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

    Column(modifier = Modifier.fillMaxSize()) {
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
                        controller.setZoom(6.0)
                        controller.setCenter(GeoPoint(defaultLat, defaultLng))

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
                                marker.title = "🔴 ${request["category"]?.toString() ?: "Talep"}"
                                marker.snippet = "Aciliyet: ${request["urgencylevel"]} | Durum: ${request["status"]}"
                                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
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

@Composable
fun VolunteerMapLegendItem(
    color: Color,
    label: String
) {
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