package com.proiectpdm

import com.proiectpdm.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureMonitoring()
    configureDI()
    configureSerialization()
    configureDatabases()
    configureRouting()

}
