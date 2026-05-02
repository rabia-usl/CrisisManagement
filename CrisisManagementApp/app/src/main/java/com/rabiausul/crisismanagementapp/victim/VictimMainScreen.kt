package com.rabiausul.crisismanagementapp.victim

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rabiausul.crisismanagementapp.ui.theme.CrisisManagementAppTheme

enum class VictimNavigation {
    DASHBOARD,
    CREATE_REQUEST,
    MY_REQUESTS,
    REQUEST_STATUS,
    EMERGENCY,
    MAP
}

@Composable
fun VictimMainScreen(onLogout: () -> Unit) {
    var currentScreen by remember { mutableStateOf(VictimNavigation.DASHBOARD) }

    when (currentScreen) {
        VictimNavigation.DASHBOARD -> VictimDashboard(
            onCreateRequestClick = { currentScreen = VictimNavigation.CREATE_REQUEST },
            onMyRequestsClick = { currentScreen = VictimNavigation.MY_REQUESTS },
            onRequestStatusClick = { currentScreen = VictimNavigation.REQUEST_STATUS },
            onEmergencyClick = { currentScreen = VictimNavigation.EMERGENCY },
            onMapClick = { currentScreen = VictimNavigation.MAP },
            onLogout = onLogout
        )
        VictimNavigation.CREATE_REQUEST -> CreateRequestScreen(
            onBack = { currentScreen = VictimNavigation.DASHBOARD }
        )
        VictimNavigation.MY_REQUESTS -> MyRequestsScreen(
            onBack = { currentScreen = VictimNavigation.DASHBOARD }
        )
        VictimNavigation.REQUEST_STATUS -> RequestStatusScreen(
            onBack = { currentScreen = VictimNavigation.DASHBOARD }
        )
        VictimNavigation.EMERGENCY -> EmergencyScreen(
            onBack = { currentScreen = VictimNavigation.DASHBOARD }
        )
        VictimNavigation.MAP -> VictimMapScreen(
            onBack = { currentScreen = VictimNavigation.DASHBOARD }
        )
    }
}

@Composable
fun VictimDashboard(
    onCreateRequestClick: () -> Unit,
    onMyRequestsClick: () -> Unit,
    onRequestStatusClick: () -> Unit,
    onEmergencyClick: () -> Unit,
    onMapClick: () -> Unit,
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
                text = "Mağdur Paneli",
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
            VictimDashboardCard(
                title = "Yardım Talep Et",
                color = Color(0xFF1E88E5),
                modifier = Modifier.weight(1f),
                onClick = onCreateRequestClick
            )
            VictimDashboardCard(
                title = "Taleplerim",
                color = Color(0xFF43A047),
                modifier = Modifier.weight(1f),
                onClick = onMyRequestsClick
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            VictimDashboardCard(
                title = "Talep Durumu",
                color = Color(0xFF8E24AA),
                modifier = Modifier.weight(1f),
                onClick = onRequestStatusClick
            )
            VictimDashboardCard(
                title = "Acil Durum",
                color = Color(0xFFE53935),
                modifier = Modifier.weight(1f),
                onClick = onEmergencyClick
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clickable { onMapClick() },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF00897B))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🗺 Yakınımdaki Kaynaklar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun VictimDashboardCard(
    title: String,
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
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VictimMainScreenPreview() {
    CrisisManagementAppTheme {
        VictimMainScreen(onLogout = {})
    }
}

@Preview(showBackground = true)
@Composable
fun VictimDashboardPreview() {
    CrisisManagementAppTheme {
        VictimDashboard(
            onCreateRequestClick = {},
            onMyRequestsClick = {},
            onRequestStatusClick = {},
            onEmergencyClick = {},
            onMapClick = {},
            onLogout = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VictimDashboardCardPreview() {
    CrisisManagementAppTheme {
        VictimDashboardCard(
            title = "Yardım Talep Et",
            color = Color(0xFF1E88E5),
            onClick = {}
        )
    }
}
