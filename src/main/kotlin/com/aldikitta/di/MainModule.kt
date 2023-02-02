package com.aldikitta.di

import com.aldikitta.controller.user.UserController
import com.aldikitta.controller.user.UserControllerImpl
import com.aldikitta.util.Constants
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mainModule = module {
    single {
        val client = KMongo.createClient().coroutine
        client.getDatabase(Constants.DATABASE_NAME)
    }
    single<UserController> {
        UserControllerImpl(get())
    }
}