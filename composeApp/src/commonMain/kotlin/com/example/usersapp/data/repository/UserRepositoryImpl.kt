package com.example.usersapp.data.repository

import com.example.usersapp.data.mapper.toModel
import com.example.usersapp.data.source.network.service.UserApiService
import com.example.usersapp.domain.model.User
import com.example.usersapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single

@Single
class UserRepositoryImpl(private val userApi: UserApiService) : UserRepository {
    override fun getUsers(page: Int): Flow<List<User>> = flow {
        val user = userApi.getUsers(page).data.toModel()
        emit(user)
    }

    override fun getUserDetails(id: Int): Flow<User> = flow {
        val user = userApi.getDetails(id).data.toModel()
        emit(user)
    }

}