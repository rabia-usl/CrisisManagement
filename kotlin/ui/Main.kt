package ui

import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import db.initDatabase
import model.User
import repository.AppRepository

fun main() = application {
    // 1. Veritabanı ve Repository Hazırlığı
    remember { initDatabase() }
    val repository = remember { AppRepository() }

    Window(onCloseRequest = ::exitApplication, title = "Afet Yönetim Sistemi v1.0") {
        var currentUser by remember { mutableStateOf<User?>(null) }

        if (currentUser == null) {
            // Giriş sayfası
            LoginScreen(repository) { user ->
                currentUser = user
            }
        } else {
            // Ana sayfa
            DashboardScreen(repository, currentUser!!)
        }
    }
}