package com.example.usersapp.presentation.users.model

import com.example.usersapp.domain.model.User

data class UserUi(val id: Int, val name: String, val avatar: String)

fun User.toUi() = UserUi(id, "$firstName $lastName", avatar)

fun List<User>.toUi() = map { it.toUi() }
