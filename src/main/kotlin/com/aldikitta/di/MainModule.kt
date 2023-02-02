package com.aldikitta.di

import com.aldikitta.data.repository.user.UserRepository
import com.aldikitta.data.repository.user.UserRepositoryImpl
import com.aldikitta.util.Constants
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mainModule = module {
    single {
        val client = KMongo.createClient().coroutine
        client.getDatabase(Constants.DATABASE_NAME)
    }
    single<UserRepository> {
        UserRepositoryImpl(get())
    }
}