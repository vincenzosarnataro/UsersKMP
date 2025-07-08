package com.example.usersapp.data.mapper

import com.example.usersapp.data.source.network.dto.UserDto
import com.example.usersapp.domain.model.User

fun UserDto.toModel() = User(
    id = id,
    email = email,
    firstName = firstName,
    lastName = lastName,
    avatar = avatar
)

fun List<UserDto>.toModel() = map { it.toModel() }