package com.rabiausul.crisismanagementapp.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.rabiausul.crisismanagementapp.model.User
import com.rabiausul.crisismanagementapp.api.RetrofitClient
import com.rabiausul.crisismanagementapp.ui.theme.AccentRed
import com.rabiausul.crisismanagementapp.ui.theme.CrisisManagementAppTheme
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: (User) -> Unit = {},
    onNavigateToRegister: () -> Unit = {}
) {
    var tcNo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Black
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = Color.Black,
            contentColor = Color.White
        ) {
            LoginScreenContent(
                tcNo = tcNo,
                onTcNoChange = { if (it.length <= 11) tcNo = it },
                password = password,
                onPasswordChange = { password = it },
                isLoading = isLoading,
                onLoginClick = {
                    if (tcNo.isEmpty() || password.isEmpty()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Lütfen tüm alanları doldurun.")
                        }
                    } else {
                        isLoading = true
                        scope.launch {
                            try {
                                val userRequest = User(
                                    identityNumber = tcNo,
                                    userPassword = password
                                )
                                val response = RetrofitClient.api.login(userRequest)
                                if (response.isSuccessful && response.body() != null) {
                                    onLoginSuccess(response.body()!!)
                                } else {
                                    snackbarHostState.showSnackbar("Giriş başarısız. Bilgilerinizi kontrol edin.")
                                }
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Bir hata oluştu: ${e.message}")
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                },
                onNavigateToRegister = onNavigateToRegister
            )
        }
    }
}

@Composable
fun LoginScreenContent(
    tcNo: String,
    onTcNoChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    onLoginClick: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Giriş Yap",
            fontSize = 28.sp,
            color = AccentRed
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = tcNo,
            onValueChange = onTcNoChange,
            label = { Text("TC Kimlik No") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedLabelColor = AccentRed,
                unfocusedLabelColor = Color.Gray,
                focusedBorderColor = AccentRed,
                unfocusedBorderColor = Color.Gray,
                cursorColor = AccentRed
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Şifre") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedLabelColor = AccentRed,
                unfocusedLabelColor = Color.Gray,
                focusedBorderColor = AccentRed,
                unfocusedBorderColor = Color.Gray,
                cursorColor = AccentRed
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Giriş Yap", color = Color.White, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToRegister) {
            Text(
                text = buildAnnotatedString {
                    append("Hesabın yok mu? ")
                    withStyle(style = SpanStyle(color = AccentRed, fontWeight = FontWeight.Bold)) {
                        append("Kayıt Ol")
                    }
                },
                color = Color.LightGray,
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    CrisisManagementAppTheme {
        Scaffold(containerColor = Color.Black) { padding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                color = Color.Black,
                contentColor = Color.White
            ) {
                LoginScreenContent(
                    tcNo = "",
                    onTcNoChange = {},
                    password = "",
                    onPasswordChange = {},
                    isLoading = false,
                    onLoginClick = {},
                    onNavigateToRegister = {}
                )
            }
        }
    }
}
