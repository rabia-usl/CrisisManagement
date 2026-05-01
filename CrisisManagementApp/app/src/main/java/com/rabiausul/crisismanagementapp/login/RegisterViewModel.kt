package com.rabiausul.crisismanagementapp.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rabiausul.crisismanagementapp.api.RetrofitClient
import com.rabiausul.crisismanagementapp.model.User
import com.rabiausul.crisismanagementapp.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

data class RegisterUiState(
    val username: String = "",
    val phonenumber: String = "",
    val userrole: UserRole? = null,
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

    fun onUserroleChange(value: UserRole) {
        Log.d("RegisterViewModel", "Role changed to: ${value.name}")
        _uiState.value = _uiState.value.copy(userrole = value)
    }

    fun onIdentitynumberChange(value: String) {
        _uiState.value = _uiState.value.copy(identitynumber = value)
    }

    fun onUserpasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(userpassword = value)
    }

    fun register() {
        val state = _uiState.value
        Log.d("RegisterViewModel", "Attempting register with: $state")

        if (state.username.isBlank() || state.phonenumber.isBlank() ||
            state.identitynumber.isBlank() || state.userpassword.isBlank() ||
            state.userrole == null) {
            _uiState.value = state.copy(errorMessage = "Lütfen tüm alanları (rol dahil) doldurun.")
            return
        }

        if (state.identitynumber.length != 11) {
            _uiState.value = state.copy(errorMessage = "TC Kimlik No 11 haneli olmalıdır.")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = "")

            try {
                // Sunucuya gönderilecek veriyi oluşturuyoruz.
                val userRequest = User(
                    userName = state.username,
                    phoneNumber = state.phonenumber,
                    userRole = state.userrole.name, // Enum ismini (İngilizce) gönderiyoruz
                    identityNumber = state.identitynumber,
                    userPassword = state.userpassword,
                    userLocation = null
                )

                val response = RetrofitClient.api.register(userRequest)

                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Hata detayı alınamadı."
                    Log.e("RegisterError", "Error Code: ${response.code()}, Body: $errorBody")
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Sunucu Hatası (${response.code()}): $errorBody"
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Bağlantı Hatası: ${e.localizedMessage}"
                )
            }
        }
    }
}
