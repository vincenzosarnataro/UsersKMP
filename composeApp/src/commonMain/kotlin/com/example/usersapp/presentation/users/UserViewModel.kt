package com.example.usersapp.presentation.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usersapp.domain.usecase.GetUsersUseCase
import com.example.usersapp.presentation.users.model.toUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class UserViewModel(
    private val getUsersUseCase: GetUsersUseCase

) : ViewModel() {

    private val _state = MutableStateFlow<UserUiState>(value = UserUiState.Loading)
    val state: StateFlow<UserUiState> = _state.onStart { getUsers() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), _state.value)


    fun onAction(action: UserAction) {
        when (action) {
            UserAction.LoadUsers -> {
                val currentState = _state.value
                if (currentState is UserUiState.Success) {
                    _state.update { currentState.copy(loadingPage = true) }
                }
                getUsers()

            }
        }
    }

    private fun getUsers() = viewModelScope.launch {
        runCatching {
            getUsersUseCase().collectLatest { users ->
                _state.update {
                    UserUiState.Success(
                        users.toUi(),
                    )
                }
            }
        }.onFailure {
            if (_state.value !is UserUiState.Success)
                _state.update { UserUiState.Error }
        }


    }

}