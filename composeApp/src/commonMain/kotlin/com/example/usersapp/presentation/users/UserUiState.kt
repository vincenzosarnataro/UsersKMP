package com.example.usersapp.presentation.users

import com.example.usersapp.presentation.users.model.UserUi

sealed interface UserUiState {
    data class Success(
        val users: List<UserUi> = emptyList(),
        val loadingPage: Boolean = false,
    ) : UserUiState

    data object Error : UserUiState
    data object Loading : UserUiState
}