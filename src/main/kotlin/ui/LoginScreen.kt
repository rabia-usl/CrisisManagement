package ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import model.User
import repository.AppRepository
import androidx.compose.ui.text.input.PasswordVisualTransformation

@Composable
fun LoginScreen(repository: AppRepository, onLoginSuccess: (User) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Afet Koordinasyon Girişi", style = MaterialTheme.typography.h4)
        Spacer(modifier = Modifier.height(24.dp))

        // LoginScreen.kt içindeki TextField kısımlarını şöyle güncelle:
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Telefon Numarası") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Şifre") },
            visualTransformation = PasswordVisualTransformation() // Şifreyi yıldızlı gösterir
        )

        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
        }

        Button(
            onClick = {
                val user = repository.login(username, phone)
                if (user != null) {
                    onLoginSuccess(user)
                } else {
                    errorMessage = "Kullanıcı adı veya telefon hatalı!"
                }
            },
            modifier = Modifier.padding(top = 16.dp).width(280.dp)
        ) {
            Text("Sisteme Giriş Yap")
        }
    }
}