package com.aldikitta.di

import com.aldikitta.data.repository.comment.CommentRepository
import com.aldikitta.data.repository.comment.CommentRepositoryImpl
import com.aldikitta.data.repository.follow.FollowRepository
import com.aldikitta.data.repository.follow.FollowRepositoryImpl
import com.aldikitta.data.repository.likes.LikeRepository
import com.aldikitta.data.repository.likes.LikeRepositoryImpl
import com.aldikitta.data.repository.post.PostRepository
import com.aldikitta.data.repository.post.PostRepositoryImpl
import com.aldikitta.data.repository.user.UserRepository
import com.aldikitta.data.repository.user.UserRepositoryImpl
import com.aldikitta.service.*
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
    single<FollowRepository> {
        FollowRepositoryImpl(get())
    }
    single<PostRepository> {
        PostRepositoryImpl(get())
    }
    single<LikeRepository> {
        LikeRepositoryImpl(get())
    }
    single<CommentRepository> {
        CommentRepositoryImpl(get())
    }
    single { UserService(get()) }
    single { FollowService(get()) }
    single { PostService(get()) }
    single { LikeService(get()) }
    single { CommentService(get()) }
}