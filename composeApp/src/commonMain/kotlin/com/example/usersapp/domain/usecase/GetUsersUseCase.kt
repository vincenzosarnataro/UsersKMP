package com.example.usersapp.domain.usecase

import com.example.usersapp.domain.model.User
import com.example.usersapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Factory

@Factory
class GetUsersUseCase(private val repository: UserRepository) {
    private val _items = MutableStateFlow<List<User>>(emptyList())
    private val items: StateFlow<List<User>> = _items.asStateFlow()
    private var currentPage = 1
    private var isLoading = false
    private var endReached = false

    suspend operator fun invoke(): Flow<List<User>> {
        if (isLoading || endReached) return items
        isLoading = true

        val result = repository.getUsers(currentPage).catch {
            isLoading = false
            throw it
        }.first()

        if (result.isEmpty()) {
            endReached = true
        } else {
            _items.value = _items.value + result
            currentPage++
        }

        isLoading = false
        return items
    }
}