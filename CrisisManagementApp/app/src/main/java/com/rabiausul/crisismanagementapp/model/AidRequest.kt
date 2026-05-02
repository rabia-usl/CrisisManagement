package com.rabiausul.crisismanagementapp.model

data class AidRequest(
    val requestId: Int = 0,
    val victimId: Int = 0,
    val category: String = "",
    val urgencyLevel: Int = 1,
    val status: String = "",
    val description: String = "",
    val vulnerableCount: Int = 0,
    val latitude: Double? = null,
    val longitude: Double? = null
)