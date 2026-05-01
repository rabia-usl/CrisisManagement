package com.crisis.disasterapp.model

data class RequestResourceMatch(
    val matchId: Int = 0,
    val requestId: Int = 0,
    val resourceId: Int = 0,
    val matchDate: String = "",
    val allocateQuantity: Int = 0
)