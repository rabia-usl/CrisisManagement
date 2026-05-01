package com.crisis.disasterapp.model

data class User(
    val userId: Int = 0,
    val userName: String = "",
    val phoneNumber: String = "",
    val userRole: String = "",
    val userPassword: String = "",
    val identityNumber: String = ""
)