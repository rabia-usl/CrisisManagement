package com.rabiausul.crisismanagementapp

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rabiausul.crisismanagementapp.login.LoginScreen
import com.rabiausul.crisismanagementapp.operator.OperatorMainScreen
import com.rabiausul.crisismanagementapp.volunteer.VolunteerMainScreen
import com.rabiausul.crisismanagementapp.victim.VictimMainScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Operator : Screen("operator")
    object Victim : Screen("victim")
    object Volunteer : Screen("volunteer")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { user ->
                    when (user.userRole?.uppercase()) {
                        "OPERATOR" -> navController.navigate(Screen.Operator.route)
                        "VOLUNTEER" -> navController.navigate(Screen.Volunteer.route)
                        "VICTIM" -> navController.navigate(Screen.Victim.route)
                        else -> navController.navigate(Screen.Victim.route)
                    }
                }
            )
        }

        composable(Screen.Operator.route) {
            OperatorMainScreen()
        }

        composable(Screen.Volunteer.route) {
            VolunteerMainScreen()
        }

        composable(Screen.Victim.route) {
            VictimMainScreen()
        }
    }
}