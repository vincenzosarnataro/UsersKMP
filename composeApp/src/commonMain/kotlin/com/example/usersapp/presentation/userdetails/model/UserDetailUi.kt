package com.example.usersapp.presentation.userdetails.model

import com.example.usersapp.domain.model.User

data class UserDetailUi(val id: Int, val name: String, val avatar: String, val email: String)

fun User.toUi() = UserDetailUi(id, "$firstName $lastName", avatar, email)

