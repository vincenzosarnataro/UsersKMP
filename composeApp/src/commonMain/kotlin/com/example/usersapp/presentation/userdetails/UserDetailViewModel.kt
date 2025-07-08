package com.example.usersapp.presentation.userdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usersapp.domain.usecase.GetUserDetailsUseCase
import com.example.usersapp.presentation.userdetails.model.toUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam

@KoinViewModel
class UserDetailViewModel(
    @InjectedParam
    private val id: Int,
    private val getUserDetailsUseCase: GetUserDetailsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<UserDetailUiState>(value = UserDetailUiState.Loading)
    val state: StateFlow<UserDetailUiState> = _state.onStart { getUserDetails() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), _state.value)

    fun getUserDetails() = viewModelScope.launch {
        getUserDetailsUseCase(id).catch { _state.update { UserDetailUiState.Error } }
            .collectLatest { user ->
                _state.update { UserDetailUiState.Success(user.toUi()) }
            }
    }


}