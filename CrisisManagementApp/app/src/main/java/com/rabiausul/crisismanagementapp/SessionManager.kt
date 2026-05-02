package com.rabiausul.crisismanagementapp

import com.rabiausul.crisismanagementapp.model.User

object SessionManager {
    private var currentUser: User? = null
    private var userLat: Double = 39.9334
    private var userLng: Double = 32.8597

    fun setUser(user: User) {
        currentUser = user
    }

    fun getUser(): User? = currentUser
    fun getUserId(): Int = currentUser?.userId ?: 0
    fun getUserRole(): String = currentUser?.userRole ?: ""
    fun getUserName(): String = currentUser?.userName ?: ""
    fun getIdentityNumber(): String = currentUser?.identityNumber ?: ""
    fun getPhoneNumber(): String = currentUser?.phoneNumber ?: ""

    fun setLocation(lat: Double, lng: Double) {
        userLat = lat
        userLng = lng
    }

    fun getLat(): Double = userLat
    fun getLng(): Double = userLng

    fun isLoggedIn(): Boolean = currentUser != null

    fun logout() {
        currentUser = null
        userLat = 39.9334
        userLng = 32.8597
    }
}