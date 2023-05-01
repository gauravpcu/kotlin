package com.example.plugins

import org.jetbrains.exposed.sql.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject

fun Application.configureDatabases() {
    val database = Database.connect(
        url = "jdbc:mysql://benchmarking.mysql.database.azure.com/sp_test",
        user = "sproot",
        password = "Procurement!2023",
        driver = "com.mysql.cj.jdbc.Driver",
    )

    /*val database = Database.connect(
        url = "jdbc:mysql://127.0.0.1:3306/sp_test",
        user = "root",
        password = "Procurement!2023",
        driver = "com.mysql.cj.jdbc.Driver",
    )*/

    val userService = UserService(database)
    routing {
        // Create user
        post("/api/user") {
            val users = call.receive<Array<User>>()
            val total = userService.create(users)
            call.respond(HttpStatusCode.Created, "Received ${users.size}, created ${total} users")
        }


        // Read user
        get("/api/user") {
            val limit = call.parameters["limit"]?.toInt() ?: throw IllegalArgumentException("Invalid number")
            val user = userService.readLimit(limit)
            if (user != null) {
                call.respond(HttpStatusCode.OK, user)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/api/user/manipulated") {
            val limit = call.parameters["limit"]?.toInt() ?: throw IllegalArgumentException("Invalid number")
            val users = userService.readLimit(limit)
            if (users != null) {

                val manipulatedUsers =  ArrayList<User>()
                users.forEach {
                    var user = it.copy(age = it.age +1, firstName = it.firstName.uppercase(),
                        lastName = it.lastName.uppercase());
                    manipulatedUsers.add(user);
                }

                call.respond(HttpStatusCode.OK, manipulatedUsers)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
        // Update user
        put("/api/user") {
            val users = call.receive<Array<User>>()
            val total = userService.update(users)
            call.respond(HttpStatusCode.OK, "Received ${users.size}, updated ${total} users")
        }
    }
}
