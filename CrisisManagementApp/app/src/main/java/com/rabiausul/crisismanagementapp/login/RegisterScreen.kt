package com.rabiausul.crisismanagementapp.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rabiausul.crisismanagementapp.viewmodel.RegisterViewModel

val AccentRed = Color(0xFFCC2B2B)

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Konum izni için launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (granted) {
            viewModel.register(context)
        } else {
            // İzin verilmezse konumsuz devam et (isteğe bağlı)
            viewModel.register(context)
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onRegisterSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Kayıt Ol",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = AccentRed
        )
        Text(
            text = "Afet Koordinasyon Sistemi",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        FormField(
            label = "Kullanıcı Adı",
            value = uiState.username,
            onValueChange = { viewModel.onUsernameChange(it) }
        )

        FormField(
            label = "Telefon Numarası",
            value = uiState.phonenumber,
            onValueChange = { viewModel.onPhonenumberChange(it) },
            keyboardType = KeyboardType.Phone
        )

        FormField(
            label = "TC Kimlik No",
            value = uiState.identitynumber,
            onValueChange = { viewModel.onIdentitynumberChange(it) },
            keyboardType = KeyboardType.Number
        )

        FormField(
            label = "Şifre",
            value = uiState.userpassword,
            onValueChange = { viewModel.onUserpasswordChange(it) },
            isPassword = true
        )

        RoleSelection(
            selectedRole = uiState.userrole,
            onRoleSelected = { viewModel.onUserroleChange(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.errorMessage.isNotBlank()) {
            Text(
                text = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Button(
            onClick = {
                // Önce konum iznini iste, sonra kayıt yap
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            },
            enabled = !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            } else {
                Text("Kayıt Ol", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentRed,
            focusedLabelColor = AccentRed
        )
    )
}

@Composable
fun RoleSelection(
    selectedRole: String,
    onRoleSelected: (String) -> Unit
) {
    val roles = listOf(
        "victim" to "Mağdur",
        "volunteer" to "Gönüllü",
        "operator" to "Operatör"
    )

    Text(
        text = "Rol",
        fontSize = 14.sp,
        color = Color.Gray,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    roles.forEach { (value, label) ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selectedRole == value,
                onClick = { onRoleSelected(value) },
                colors = RadioButtonDefaults.colors(selectedColor = AccentRed)
            )
            Text(
                text = label,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
}