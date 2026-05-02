package com.rabiausul.crisismanagementapp.victim

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
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
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.rabiausul.crisismanagementapp.SessionManager
import com.rabiausul.crisismanagementapp.api.RetrofitClient
import com.rabiausul.crisismanagementapp.model.AidRequest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRequestScreen(onBack: () -> Unit) {
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var urgencyLevel by remember { mutableStateOf(1) }
    var vulnerableCount by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val categories = listOf("Gıda", "Su", "İlaç", "Giysi", "Barınak", "Tıbbi Yardım", "Diğer")

    // İzin verildikten sonra çalışacak fonksiyon
    suspend fun submitRequest() {
        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            val location = fusedLocationClient
                .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .await()

            val locationString = if (location != null) {
                "POINT(${location.longitude} ${location.latitude})"
            } else null

            val request = AidRequest(
                victimId = SessionManager.getUserId(),
                category = category,
                description = description,
                urgencyLevel = urgencyLevel,
                vulnerableCount = vulnerableCount.toIntOrNull() ?: 0,
                latitude = location?.latitude,
                longitude = location?.longitude            )

            val response = RetrofitClient.api.createRequest(request)
            if (response.isSuccessful) {
                snackbarHostState.showSnackbar("Talebiniz başarıyla oluşturuldu")
                category = ""
                description = ""
                urgencyLevel = 1
                vulnerableCount = ""
            } else {
                snackbarHostState.showSnackbar("Talep oluşturulamadı: ${response.code()}")
            }
        } catch (e: Exception) {
            snackbarHostState.showSnackbar("Bağlantı hatası: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    // Konum izni launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (granted) {
            isLoading = true
            scope.launch { submitRequest() }
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Konum izni gereklidir.")
                isLoading = false
            }
        }
    }

    // Butona basınca önce izin kontrol et
    fun onSubmitClick() {
        if (category.isEmpty() || description.isEmpty() || vulnerableCount.isEmpty()) {
            scope.launch { snackbarHostState.showSnackbar("Lütfen tüm alanları doldurun") }
            return
        }

        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineLocationGranted || coarseLocationGranted) {
            // İzin zaten var, direkt devam et
            isLoading = true
            scope.launch { submitRequest() }
        } else {
            // İzin yok, iste
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
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
                    text = "Yardım Talep Et",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text(text = "Kategori:", fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Kategori Seç") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        category = cat
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Açıklama:", fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Durumunuzu açıklayın") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 5
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Aciliyet Seviyesi:", fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(1 to "Düşük", 2 to "Orta", 3 to "Yüksek").forEach { (level, label) ->
                            val color = when (level) {
                                1 -> Color(0xFF43A047)
                                2 -> Color(0xFFFB8C00)
                                else -> Color(0xFFE53935)
                            }
                            FilterChip(
                                selected = urgencyLevel == level,
                                onClick = { urgencyLevel = level },
                                label = { Text(label) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = color,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Etkilenen Kişi Sayısı:", fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = vulnerableCount,
                        onValueChange = { vulnerableCount = it },
                        label = { Text("Kişi sayısı") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { onSubmitClick() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Talep Oluştur", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}