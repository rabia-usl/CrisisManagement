package ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import model.DisasterRequest
import model.User
import repository.AppRepository

@Composable
fun DashboardScreen(repository: AppRepository, currentUser: User) {
    var requests by remember { mutableStateOf(listOf<DisasterRequest>()) }

    // Verileri tazeleme fonksiyonu
    fun refresh() { requests = repository.getAllRequests() }

    LaunchedEffect(Unit) { refresh() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hoş geldin, ${currentUser.name}") },
                actions = {
                    Text(currentUser.role, modifier = Modifier.padding(end = 16.dp))
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Buraya daha önce yazdığımız Form ve Liste kodlarını ekleyebilirsin
            Text("Aktif Talepler", style = MaterialTheme.typography.h5, modifier = Modifier.padding(16.dp))

            LazyColumn {
                //items(requests) { request -> RequestCard(request)
            }
        }
    }
}
