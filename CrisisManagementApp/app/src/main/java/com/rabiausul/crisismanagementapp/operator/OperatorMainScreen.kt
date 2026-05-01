package com.rabiausul.crisismanagementapp.operator

import androidx.compose.foundation.clickable
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

enum class OperatorNavigation {
    DASHBOARD,
    REQUESTS,
    RESOURCES,
    MATCHES,
    PENDING
}

@Composable
fun OperatorMainScreen(onLogout: () -> Unit) {
    var currentScreen by remember { mutableStateOf(OperatorNavigation.DASHBOARD) }

    when (currentScreen) {
        OperatorNavigation.DASHBOARD -> OperatorDashboard(
            onRequestsClick = { currentScreen = OperatorNavigation.REQUESTS },
            onResourcesClick = { currentScreen = OperatorNavigation.RESOURCES },
            onMatchesClick = { currentScreen = OperatorNavigation.MATCHES },
            onPendingClick = { currentScreen = OperatorNavigation.PENDING },
            onLogout = onLogout
        )
        OperatorNavigation.REQUESTS -> RequestListScreen(
            onBack = { currentScreen = OperatorNavigation.DASHBOARD }
        )
        OperatorNavigation.RESOURCES -> ResourceListScreen(
            onBack = { currentScreen = OperatorNavigation.DASHBOARD }
        )
        OperatorNavigation.MATCHES -> MatchScreen(
            onBack = { currentScreen = OperatorNavigation.DASHBOARD }
        )
        OperatorNavigation.PENDING -> PendingRequestsScreen(
            onBack = { currentScreen = OperatorNavigation.DASHBOARD }
        )
    }
}

@Composable
fun OperatorDashboard(
    onRequestsClick: () -> Unit,
    onResourcesClick: () -> Unit,
    onMatchesClick: () -> Unit,
    onPendingClick: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Operatör Paneli",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            TextButton(
                onClick = onLogout,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFFE53935)
                )
            ) {
                Text("Çıkış Yap")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OperatorDashboardCard(
                title = "Yardım Talepleri",
                count = "0",
                color = Color(0xFFE53935),
                modifier = Modifier.weight(1f),
                onClick = onRequestsClick
            )
            OperatorDashboardCard(
                title = "Kaynaklar",
                count = "0",
                color = Color(0xFF1E88E5),
                modifier = Modifier.weight(1f),
                onClick = onResourcesClick
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OperatorDashboardCard(
                title = "Eşleştirmeler",
                count = "0",
                color = Color(0xFF43A047),
                modifier = Modifier.weight(1f),
                onClick = onMatchesClick
            )
            OperatorDashboardCard(
                title = "Bekleyenler",
                count = "0",
                color = Color(0xFFFB8C00),
                modifier = Modifier.weight(1f),
                onClick = onPendingClick
            )
        }
    }
}

@Composable
fun OperatorDashboardCard(
    title: String,
    count: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = title,
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}