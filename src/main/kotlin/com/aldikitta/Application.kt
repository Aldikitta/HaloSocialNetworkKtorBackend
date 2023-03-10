package com.aldikitta

import com.aldikitta.di.mainModule
import io.ktor.server.application.*
import com.aldikitta.plugins.*
import org.koin.ktor.plugin.Koin
import java.nio.file.Paths

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    install(Koin) {
        modules(mainModule)
    }
    configureSerialization()
//    configureSockets()
    configureMonitoring()
    configureHTTP()
    configureSecurity()
    configureRouting()
}
