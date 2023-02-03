package com.aldikitta.routes

import com.aldikitta.data.models.User
import com.aldikitta.data.requests.CreateAccountRequest
import com.aldikitta.data.responses.BasicApiResponse
import com.aldikitta.di.testModule
import com.aldikitta.repository.user.FakeUserRepository
import com.aldikitta.util.ApiResponseMessages
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals


internal class CreateUserRouteTest : KoinTest {
    private val userRepository by inject<FakeUserRepository>()
    private val gson = Gson()

    @BeforeTest
    fun setup() {
        startKoin {
            modules(testModule)
        }

    }

    @AfterTest
    fun tearDown(){
        stopKoin()
    }
    @Test
    fun `Create user, no body attached, responds with BadRequest`() {
        withTestApplication(
            moduleFunction = {
                install(Routing) {
                    createUser(userRepository = userRepository)
                }
                install(ContentNegotiation) { gson { } }
            }
        ) {
            val request = handleRequest(
                method = HttpMethod.Post,
                uri = "/api/user/create",
                setup = {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
            )
            assertEquals(HttpStatusCode.BadRequest, request.response.status())
        }
    }

    @Test
    fun `Create user, user already exists, responds with unsuccessful`() = runBlocking {
        val user = User(
            email = "test@email.com",
            username = "test",
            password = "123",
            profileImageUrl = "",
            bio = "",
            githubUrl = null,
            instagramUrl = null,
            linkedinUrl = null
        )
        userRepository.createUser(
            user
        )
        withTestApplication(
            moduleFunction = {
                install(Routing) {
                    createUser(userRepository = userRepository)
                }
                install(ContentNegotiation) { gson { } }
            }
        ) {
            val request = handleRequest(
                method = HttpMethod.Post,
                uri = "/api/user/create"
            ){
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                val request = CreateAccountRequest(
                    email = "test@email.com",
                    username = "test",
                    password = "123",
                )
                setBody(gson.toJson(request))
            }
            val response = gson.fromJson(
                request.response.content ?: "",
                BasicApiResponse::class.java
            )
            assertThat(response.successful).isFalse()
            assertThat(response.message).isEqualTo(ApiResponseMessages.USER_ALREADY_EXIST)
        }
    }

    @Test
    fun `Create user, field blank responds with unsuccessful`() {
          withTestApplication(
            moduleFunction = {
                install(Routing) {
                    createUser(userRepository = userRepository)
                }
                install(ContentNegotiation) { gson { } }
            }
        ) {
            val request = handleRequest(
                method = HttpMethod.Post,
                uri = "/api/user/create"
            ){
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                val request = CreateAccountRequest(
                    email = "",
                    username = "test",
                    password = "123"
                )
                setBody(gson.toJson(request))
            }
            val response = gson.fromJson(
                request.response.content ?: "",
                BasicApiResponse::class.java
            )
            assertThat(response.successful).isFalse()
            assertThat(response.message).isEqualTo(ApiResponseMessages.FIELD_BLANK)
        }
    }


    @Test
    fun `Create user, valid data, responds with success`() {
        withTestApplication(
            moduleFunction = {
                install(Routing) {
                    createUser(userRepository = userRepository)
                }
                install(ContentNegotiation) { gson { } }
            }
        ) {
            val request = handleRequest(
                method = HttpMethod.Post,
                uri = "/api/user/create"
            ){
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                val request = CreateAccountRequest(
                    email = "test@test.com",
                    username = "test",
                    password = "123"
                )
                setBody(gson.toJson(request))
            }
            val response = gson.fromJson(
                request.response.content ?: "",
                BasicApiResponse::class.java
            )
            assertThat(response.successful).isTrue()

            runBlocking {
                val isUserDb = userRepository.getUserByEmail("test@test.com") != null
                assertThat(isUserDb).isTrue()
            }

        }
    }
}