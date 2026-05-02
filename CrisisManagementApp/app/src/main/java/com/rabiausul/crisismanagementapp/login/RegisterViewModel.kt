package com.rabiausul.crisismanagementapp.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.rabiausul.crisismanagementapp.api.RetrofitClient
import com.rabiausul.crisismanagementapp.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class RegisterUiState(
    val username: String = "",
    val phonenumber: String = "",
    val userrole: String = "victim",
    val identitynumber: String = "",
    val userpassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val isSuccess: Boolean = false
)

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun onUsernameChange(value: String) {
        _uiState.value = _uiState.value.copy(username = value)
    }

    fun onPhonenumberChange(value: String) {
        _uiState.value = _uiState.value.copy(phonenumber = value)
    }

    fun onUserroleChange(value: String) {
        _uiState.value = _uiState.value.copy(userrole = value)
    }

    fun onIdentitynumberChange(value: String) {
        _uiState.value = _uiState.value.copy(identitynumber = value)
    }

    fun onUserpasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(userpassword = value)
    }

    @SuppressLint("MissingPermission")
    fun register(context: Context) {
        val state = _uiState.value

        if (state.username.isBlank() || state.phonenumber.isBlank() ||
            state.identitynumber.isBlank() || state.userpassword.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Lütfen tüm alanları doldurun.")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = "")

            try {
                // GPS'ten konumu al
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                val location = fusedLocationClient
                    .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .await()

                // Konum "POINT(longitude latitude)" formatında gönderilir (PostGIS standardı)
                val locationString = if (location != null) {
                    "POINT(${location.longitude} ${location.latitude})"
                } else {
                    null
                }

                val user = User(
                    userName = state.username,
                    phoneNumber = state.phonenumber,
                    userRole = state.userrole,
                    identityNumber = state.identitynumber,
                    userPassword = state.userpassword,
                    userLocation = locationString
                )

                val response = RetrofitClient.api.register(user)

                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Kayıt başarısız: ${response.code()}"
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Bağlantı hatası: ${e.message}"
                )
            }
        }
    }
}