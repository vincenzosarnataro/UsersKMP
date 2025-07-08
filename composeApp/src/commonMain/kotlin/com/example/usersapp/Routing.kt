package com.example.usersapp

import kotlinx.serialization.Serializable

sealed interface Routing {
    @Serializable
    object Start : Routing

    @Serializable
    data class Detail(val id: Int) : Routing
}