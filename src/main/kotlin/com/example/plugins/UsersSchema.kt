package com.example.plugins

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import kotlinx.serialization.Serializable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*

@Serializable
data class User(val id:Int=0, val firstName: String, val lastName: String, val email: String, val password: String, val age: Int)
class UserService(private val database: Database) {
    object KotlinUsers : Table() {
        val id = integer("id").autoIncrement()
        val firstName    = varchar("firstName", length = 50)
        val lastName = varchar("lastName", length = 50)
        val email = varchar("email", length = 50)
        val password = varchar("password", length = 50)
        val age = integer("age")
        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(KotlinUsers)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(user: User): Int = dbQuery {
        KotlinUsers.insert {
            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[email] = user.email
            it[password] = user.password
            it[age] = user.age
        }[KotlinUsers.id]
    }

    suspend fun create(users: Array<User>): Int  {

        val total = users.count()
        var totalCreated = 0;
        if(total > 0) {
            users.forEach {
                create(it)
                totalCreated += 1;
            }
        }
        return totalCreated;
    }



    suspend fun read(id: Int): User? {
        return dbQuery {
            KotlinUsers.select { KotlinUsers.id eq id }
                .map { User(it[KotlinUsers.id], it[KotlinUsers.firstName], it[KotlinUsers.lastName], it[KotlinUsers.email], it[KotlinUsers.password], it[KotlinUsers.age]) }
                .singleOrNull()
        }
    }

    suspend fun readLimit(limit: Int): List<User>? {
        return dbQuery {
            KotlinUsers.selectAll().limit(limit).
            map { User(it[KotlinUsers.id], it[KotlinUsers.firstName], it[KotlinUsers.lastName], it[KotlinUsers.email], it[KotlinUsers.password], it[KotlinUsers.age]) }
                .toList()
        }
    }

    suspend fun update(id: Int, user: User) {
        dbQuery {
            KotlinUsers.update({ KotlinUsers.id eq id }) {
                it[firstName] = user.firstName
                it[lastName] = user.lastName
                it[email] = user.email
                it[password] = user.password
                it[age] = user.age
            }
        }
    }

    suspend fun update(users: Array<User>):Int {
        val total = users.count()
        var totalUpdated = 0;
        if(total > 0) {
            users.forEach {
                update(it.id, it)
                totalUpdated++;
            }
        }
        return totalUpdated;
    }

    suspend fun delete(id: Int) {
        dbQuery {
            KotlinUsers.deleteWhere { KotlinUsers.id.eq(id) }
        }
    }
}