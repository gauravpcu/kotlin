package com.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.example.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::module)
        .start(wait = true)


}

fun Application.module() {
    configureSerialization()
    configureDatabases()
    configureRouting()
}


/*
* curl http://localhost:8080/api/user --include --header "Content-Type: application/json" --request "POST" --data '[{

    	"firstName": "Test",

        "lastName": "one",

        "email": "test1@example.com",

        "password": "123456",

        "age": 5

    },

    {

        "firstName": "Test",

        "lastName": "two",

        "email": "test2@example.com",

        "password": "123456",

        "age": 6

    }]'




curl http://localhost:8080/api/user --include --header "Content-Type: application/json" --request "PUT" --data '[
{
    "id": 621,
   "firstName": "UTest",
"lastName": "Uone",
    "email": "test1@example.co.in",
    "password": "123456",
    "age": 5
  },
  {
    "id": 600,
    "firstName": "UTest",
    "lastName": "Utwo",
    "email": "test2@example.co.in",
    "password": "123456",
    "age": 6
  }
]'

*
* */