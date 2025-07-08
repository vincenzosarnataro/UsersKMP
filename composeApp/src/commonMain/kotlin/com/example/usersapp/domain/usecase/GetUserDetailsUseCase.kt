package com.example.usersapp.domain.usecase

import com.example.usersapp.domain.model.User
import com.example.usersapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetUserDetailsUseCase(private val repository: UserRepository) {
    operator fun invoke(id: Int): Flow<User> = repository.getUserDetails(id)
}