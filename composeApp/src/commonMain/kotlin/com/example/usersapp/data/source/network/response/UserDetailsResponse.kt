package com.example.usersapp.data.source.network.response

import com.example.usersapp.data.source.network.dto.UserDto
import kotlinx.serialization.Serializable

@Serializable
data class UserDetailsResponse(
    val data: UserDto
)
