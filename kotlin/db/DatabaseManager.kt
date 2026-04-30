package db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

// 1. Users Tablosu
object Users : Table("users") {
    val userId = integer("userid").autoIncrement()
    val userName = varchar("username", 100)
    val phoneNumber = varchar("phonenumber", 10)
    val password = varchar("password", 50)
    val userRole = varchar("userrole", 50)
    val userLocation = varchar("userlocation", 200)
    override val primaryKey = PrimaryKey(userId)
}

// 2. Request Tablosu
object Requests : Table("request") {
    val requestId = integer("requestid").autoIncrement()
    val victimId = integer("victimid") references Users.userId
    val category = varchar("category", 100)
    val urgencyLevel = integer("urgencylevel")
    val status = varchar("status", 50)
    val description = varchar("description", 200)
    val times = datetime("times")
    val vulnerableCount = integer("vulnerablecount")
    override val primaryKey = PrimaryKey(requestId)
}

// 3. Resources Tablosu
object Resources : Table("resources") {
    val resourceId = integer("resourceid").autoIncrement()
    val providerId = integer("providerid") references Users.userId
    val category = varchar("category", 100)
    val initialQuantity = integer("initialquantity")
    val currentQuantity = integer("currentquantity")
    val resourceLocation = varchar("resourcelocation", 200)
    override val primaryKey = PrimaryKey(resourceId)
}

// 4. Assignments Tablosu
object Assignments : Table("assignments") {
    val assignmentId = integer("assignmentid").autoIncrement()
    val requestId = integer("requestid") references Requests.requestId
    val quantity = integer("quantity")
    val status = varchar("status", 50)
    override val primaryKey = PrimaryKey(assignmentId)
}

// 5. RequestResourceMatches Tablosu
object RequestResourceMatches : Table("requestresourcematches") {
    val matchId = integer("matchid").autoIncrement()
    val requestId = integer("requestid") references Requests.requestId
    val resourceId = integer("resourceid") references Resources.resourceId
    val matchDate = datetime("matchdate")
    val allocateQuantity = integer("allocatequantity")
    override val primaryKey = PrimaryKey(matchId)
}

// Veritabanı Bağlantı Fonksiyonu
fun initDatabase() {
    // URL, kullanıcı adı ve şifreyi kendi PostgreSQL ayarlarına göre düzenle
    Database.connect(
        url = "jdbc:postgresql://localhost:5432/postgres",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "030930"
    )

    transaction {
        SchemaUtils.create(Users, Requests, Resources, Assignments, RequestResourceMatches)
    }
}