package com.rabiausul.crisismanagementapp.model

data class Resource(
    val resourceId: Int = 0,
    val providerId: Int = 0,
    val category: String = "",
    val initialQuantity: Int = 0,
    val currentQuantity: Int = 0,
    val latitude: Double? = null,
    val longitude: Double? = null
)