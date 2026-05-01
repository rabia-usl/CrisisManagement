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
import com.rabiausul.crisismanagementapp.model.User
import com.rabiausul.crisismanagementapp.api.RetrofitClient
import com.rabiausul.crisismanagementapp.ui.theme.CrisisManagementAppTheme
import kotlinx.coroutines.launch

enum class UserRole { USER, OPERATOR }

@Composable
fun LoginScreen(onLoginSuccess: (User) -> Unit = {}) {
    var tcNo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.USER) }

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
                    color = Color(0xFFC0392B)
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = tcNo,
                    onValueChange = { if (it.length <= 11) tcNo = it },
                    label = { Text("TC Kimlik No") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Şifre") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Rol Seçin:", fontSize = 14.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedRole == UserRole.USER,
                            onClick = { selectedRole = UserRole.USER },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFFC0392B),
                                unselectedColor = Color.White
                            )
                        )
                        Text("Kullanıcı")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedRole == UserRole.OPERATOR,
                            onClick = { selectedRole = UserRole.OPERATOR },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFFC0392B),
                                unselectedColor = Color.White
                            )
                        )
                        Text("Operatör")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (tcNo.isEmpty() || password.isEmpty()) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Lütfen tüm alanları doldurun.")
                            }
                            return@Button
                        }

                        isLoading = true
                        scope.launch {
                            try {
                                val userRequest = User(
                                    identityNumber = tcNo,
                                    userPassword = password,
                                    userRole = selectedRole.name
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
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B)),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Giriş Yap", color = Color.White)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    CrisisManagementAppTheme {
        LoginScreen()
    }
}
