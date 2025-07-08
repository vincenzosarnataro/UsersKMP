package com.example.usersapp.presentation.userdetails

import com.example.usersapp.presentation.userdetails.model.UserDetailUi

sealed interface UserDetailUiState {
    data class Success(
        val user: UserDetailUi,
    ) : UserDetailUiState

    data object Error : UserDetailUiState
    data object Loading : UserDetailUiState
}