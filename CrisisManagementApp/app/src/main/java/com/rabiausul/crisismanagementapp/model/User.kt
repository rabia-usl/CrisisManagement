package com.rabiausul.crisismanagementapp.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("userId") val userId: Int? = null,
    @SerializedName("userName") val userName: String = "",
    @SerializedName("phoneNumber") val phoneNumber: String = "",
    @SerializedName("userRole") val userRole: String = "",
    @SerializedName("userPassword") val userPassword: String = "",
    @SerializedName("identityNumber") val identityNumber: String = "",
    @SerializedName("latitude") val latitude: Double? = null,
    @SerializedName("longitude") val longitude: Double? = null,

)
