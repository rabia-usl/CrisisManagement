package com.rabiausul.crisismanagementapp.volunteer

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

enum class VolunteerNavigation {
    DASHBOARD,
    TASKS,
    ADD_RESOURCE,
    REQUESTS,
    ASSIGNED_TASKS
}

@Composable
fun VolunteerMainScreen() {
    var currentScreen by remember { mutableStateOf(VolunteerNavigation.DASHBOARD) }

    when (currentScreen) {
        VolunteerNavigation.DASHBOARD -> VolunteerDashboard(
            onTasksClick = { currentScreen = VolunteerNavigation.TASKS },
            onAddResourceClick = { currentScreen = VolunteerNavigation.ADD_RESOURCE },
            onRequestsClick = { currentScreen = VolunteerNavigation.REQUESTS },
            onProfileClick = { currentScreen = VolunteerNavigation.ASSIGNED_TASKS }
        )
        VolunteerNavigation.TASKS -> VolunteerTasksScreen(
            onBack = { currentScreen = VolunteerNavigation.DASHBOARD }
        )
        VolunteerNavigation.ADD_RESOURCE -> AddResourceScreen(
            onBack = { currentScreen = VolunteerNavigation.DASHBOARD }
        )
        VolunteerNavigation.REQUESTS -> VolunteerRequestsScreen(
            onBack = { currentScreen = VolunteerNavigation.DASHBOARD }
        )
        VolunteerNavigation.ASSIGNED_TASKS -> AssignedTasksScreen(
            onBack = { currentScreen = VolunteerNavigation.DASHBOARD }
        )
    }
}

@Composable
fun VolunteerDashboard(
    onTasksClick: () -> Unit,
    onAddResourceClick: () -> Unit,
    onRequestsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Gönüllü Paneli",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            VolunteerDashboardCard(
                title = "Görevlerim",
                color = Color(0xFF1E88E5),
                modifier = Modifier.weight(1f),
                onClick = onTasksClick
            )
            VolunteerDashboardCard(
                title = "Kaynak Ekle",
                color = Color(0xFF43A047),
                modifier = Modifier.weight(1f),
                onClick = onAddResourceClick
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            VolunteerDashboardCard(
                title = "Talepler",
                color = Color(0xFFE53935),
                modifier = Modifier.weight(1f),
                onClick = onRequestsClick
            )
            VolunteerDashboardCard(
                title = "Profilim",
                color = Color(0xFF8E24AA),
                modifier = Modifier.weight(1f),
                onClick = onProfileClick
            )
        }
    }
}

@Composable
fun VolunteerDashboardCard(
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