package com.example.usersapp.domain.repository

import com.example.usersapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUsers(page: Int): Flow<List<User>>

    fun getUserDetails(id: Int): Flow<User>
}