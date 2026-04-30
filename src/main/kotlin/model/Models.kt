package model

import java.time.LocalDateTime

// Kullanıcı bilgilerini taşımak için
data class User(
    val id: Int,
    val name: String,
    val phone: String,
    val password: String,
    val role: String,
    val location: String
)

// Yardım taleplerini taşımak için
data class DisasterRequest(
    val id: Int,
    val victimId: Int,
    val category: String,
    val urgencyLevel: Int,
    val status: String,
    val description: String,
    val time: LocalDateTime,
    val vulnerableCount: Int
)

// Kaynak (Erzak, İlaç vb.) bilgilerini taşımak için
data class Resource(
    val id: Int,
    val providerId: Int,
    val category: String,
    val initialQuantity: Int,
    val currentQuantity: Int,
    val location: String
)

// Eşleşme (Match) bilgilerini taşımak için
data class ResourceMatch(
    val id: Int,
    val requestId: Int,
    val resourceId: Int,
    val matchDate: LocalDateTime,
    val quantity: Int
)