package com.rabiausul.crisismanagementapp

import com.rabiausul.crisismanagementapp.model.User

object SessionManager {
    private var currentUser: User? = null

    fun setUser(user: User) {
        currentUser = user
    }

    fun getUser(): User? {
        return currentUser
    }

    fun getUserId(): Int {
        return currentUser?.userId ?: 0
    }

    fun getUserRole(): String {
        return currentUser?.userRole ?: ""
    }

    fun getUserName(): String {
        return currentUser?.userName ?: ""
    }

    fun getIdentityNumber(): String {
        return currentUser?.identityNumber ?: ""
    }

    fun getPhoneNumber(): String {
        return currentUser?.phoneNumber ?: ""
    }

    fun isLoggedIn(): Boolean {
        return currentUser != null
    }

    fun logout() {
        currentUser = null
    }
}