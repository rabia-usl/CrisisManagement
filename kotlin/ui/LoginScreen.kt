package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import model.User
import repository.AppRepository

@Composable
fun LoginScreen(repository: AppRepository, onLoginSuccess: (User) -> Unit) {
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // En dış katman: Tamamen Siyah
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Arka plan simsiyah
            .padding(horizontal = 40.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Başlık
        Text(
            text = "AFET KOORDİNASYON",
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White // Siyah üzerinde beyaz yazı
        )

        Text(
            text = "Yönetim Paneline Giriş",
            style = MaterialTheme.typography.subtitle1,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Telefon Girişi (Kutucuksuz, şeffaf arka planlı stil)
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Telefon Numarası", color = Color.Gray) },
            modifier = Modifier.width(350.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF1A73E8),
                unfocusedBorderColor = Color.DarkGray,
                textColor = Color.White,
                cursorColor = Color.White
            ),
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Color.Gray) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Şifre Girişi
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Şifre", color = Color.Gray) },
            modifier = Modifier.width(350.dp),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF1A73E8),
                unfocusedBorderColor = Color.DarkGray,
                textColor = Color.White,
                cursorColor = Color.White
            ),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray) }
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color(0xFFEF5350),
                modifier = Modifier.padding(top = 12.dp),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Giriş Butonu
        Button(
            onClick = {
                val user = repository.login(phone, password)
                if (user != null) {
                    onLoginSuccess(user)
                } else {
                    errorMessage = "Telefon veya şifre hatalı!"
                }
            },
            modifier = Modifier.width(350.dp).height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1A73E8))
        ) {
            Text("Giriş Yap", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}