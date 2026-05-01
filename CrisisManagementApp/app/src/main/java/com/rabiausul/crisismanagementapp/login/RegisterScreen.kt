package com.rabiausul.crisismanagementapp.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rabiausul.crisismanagementapp.ui.theme.AccentRed
import com.rabiausul.crisismanagementapp.ui.theme.CrisisManagementAppTheme
import com.rabiausul.crisismanagementapp.login.RegisterViewModel
import com.rabiausul.crisismanagementapp.login.RegisterUiState
import com.rabiausul.crisismanagementapp.model.UserRole

// Removed local AccentRed as it is now in ui.theme

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onRegisterSuccess()
    }

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
            RegisterScreenContent(
                uiState = uiState,
                onUsernameChange = { viewModel.onUsernameChange(it) },
                onPhonenumberChange = { viewModel.onPhonenumberChange(it) },
                onIdentitynumberChange = { if (it.length <= 11) viewModel.onIdentitynumberChange(it) },
                onUserpasswordChange = { viewModel.onUserpasswordChange(it) },
                onUserroleChange = { viewModel.onUserroleChange(it) },
                onRegisterClick = { viewModel.register() },
                onNavigateToLogin = onNavigateToLogin
            )
        }
    }
}

@Composable
fun RegisterScreenContent(
    uiState: RegisterUiState,
    onUsernameChange: (String) -> Unit,
    onPhonenumberChange: (String) -> Unit,
    onIdentitynumberChange: (String) -> Unit,
    onUserpasswordChange: (String) -> Unit,
    onUserroleChange: (UserRole) -> Unit,
    onRegisterClick: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
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
            color = Color.LightGray,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        FormField(
            label = "Kullanıcı Adı",
            value = uiState.username,
            onValueChange = onUsernameChange
        )

        FormField(
            label = "Telefon Numarası",
            value = uiState.phonenumber,
            onValueChange = onPhonenumberChange,
            keyboardType = KeyboardType.Phone
        )

        FormField(
            label = "TC Kimlik No",
            value = uiState.identitynumber,
            onValueChange = onIdentitynumberChange,
            keyboardType = KeyboardType.Number
        )

        FormField(
            label = "Şifre",
            value = uiState.userpassword,
            onValueChange = onUserpasswordChange,
            isPassword = true
        )

        RoleSelection(
            selectedRole = uiState.userrole,
            onRoleSelected = onUserroleChange
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
            onClick = onRegisterClick,
            enabled = !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            } else {
                Text("Kayıt Ol", fontSize = 16.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onNavigateToLogin,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = buildAnnotatedString {
                    append("Hesabın var mı? ")
                    withStyle(style = SpanStyle(color = AccentRed, fontWeight = FontWeight.Bold)) {
                        append("Giriş Yap")
                    }
                },
                color = Color.LightGray,
                fontSize = 14.sp
            )
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
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = AccentRed,
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = AccentRed,
            unfocusedLabelColor = Color.Gray,
            cursorColor = AccentRed
        )
    )
}

@Composable
fun RoleSelection(
    selectedRole: UserRole?,
    onRoleSelected: (UserRole) -> Unit
) {
    val roles = listOf(
        UserRole.VICTIM to "Mağdur",
        UserRole.VOLUNTEER to "Gönüllü",
        UserRole.OPERATOR to "Operatör"
    )

    Text(
        text = "Rol",
        fontSize = 14.sp,
        color = Color.LightGray,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    roles.forEach { (roleEnum, label) ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selectedRole == roleEnum,
                onClick = { onRoleSelected(roleEnum) },
                colors = RadioButtonDefaults.colors(
                    selectedColor = AccentRed,
                    unselectedColor = Color.White
                )
            )
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    CrisisManagementAppTheme {
        Scaffold(containerColor = Color.Black) { padding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                color = Color.Black,
                contentColor = Color.White
            ) {
                RegisterScreenContent(
                    uiState = RegisterUiState(),
                    onUsernameChange = {},
                    onPhonenumberChange = {},
                    onIdentitynumberChange = {},
                    onUserpasswordChange = {},
                    onUserroleChange = {},
                    onRegisterClick = {},
                    onNavigateToLogin = {}
                )
            }
        }
    }
}
