package com.example.usersapp.core.di.modules

import com.example.usersapp.data.source.network.plugin.userAuthPlugin
import com.example.usersapp.data.source.network.service.UserApiService
import com.example.usersapp.data.source.network.service.createUserApiService
import de.jensklingenberg.ktorfit.Ktorfit
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class NetworkModule {
    object Constants {
        const val BASE_URL = "https://reqres.in/api/"
    }

    @Single
    fun provideHttpClient(): HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Napier.v(message)
                }
            }
            level = LogLevel.ALL
        }
        install(userAuthPlugin)
    }.also { Napier.base(DebugAntilog()) }

    @Single
    fun provideKtorfit(client: HttpClient): Ktorfit =
        Ktorfit.Builder()
            .baseUrl(Constants.BASE_URL)
            .httpClient(client)
            .build()

    @Single
    fun provideUserApiService(ktorfit: Ktorfit): UserApiService =
        ktorfit.createUserApiService()
}