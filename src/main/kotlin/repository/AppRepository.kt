package repository

import db.*
import model.User
import model.DisasterRequest
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class AppRepository {


    // repository/AppRepository.kt dosyasının içi
    fun login(phone: String, pass: String): User? {
        return transaction {
            Users.selectAll().where {
                (Users.phoneNumber eq phone) and (Users.password eq pass)
            }.map {
                User(
                    id = it[Users.userId],
                    name = it[Users.userName],
                    phone = it[Users.phoneNumber],
                    password = it[Users.password],
                    role = it[Users.userRole],
                    location = it[Users.userLocation]
                )
            }.singleOrNull()
        }
    }

    // Yeni bir kullanıcı (Mağdur veya Yardımsever) eklemek için
    fun addUser(name: String, phone: String, role: String, location: String) {
        transaction {
            Users.insert {
                it[userName] = name
                it[phoneNumber] = phone
                it[userRole] = role
                it[userLocation] = location
            }
        }
    }

    // Veritabanındaki tüm yardım taleplerini çekmek için
    fun getAllRequests(): List<DisasterRequest> {
        return transaction {
            Requests.selectAll().map {
                DisasterRequest(
                    id = it[Requests.requestId],
                    victimId = it[Requests.victimId],
                    category = it[Requests.category],
                    urgencyLevel = it[Requests.urgencyLevel],
                    status = it[Requests.status],
                    description = it[Requests.description],
                    time = it[Requests.times],
                    vulnerableCount = it[Requests.vulnerableCount]
                )
            }
        }
    }

    // Yeni bir kaynak (Erzak, Çadır vb.) eklemek için
    fun addResource(providerId: Int, cat: String, qty: Int, loc: String) {
        transaction {
            Resources.insert {
                it[Resources.providerId] = providerId
                it[category] = cat
                it[initialQuantity] = qty
                it[currentQuantity] = qty
                it[resourceLocation] = loc
            }
        }
    }

    fun addRequest(victimId: Int, category: String, urgency: Int, desc: String) {
        transaction {
            Requests.insert {
                it[Requests.victimId] = victimId
                it[Requests.category] = category
                it[Requests.urgencyLevel] = urgency
                it[Requests.status] = "Pending"
                it[Requests.description] = desc
                it[Requests.times] = java.time.LocalDateTime.now()
                it[Requests.vulnerableCount] = 1
            }
        }
    }
}