package com.example.usersapp.presentation.users

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.annotation.ExperimentalCoilApi
import com.example.usersapp.presentation.component.UserItem
import com.example.usersapp.presentation.users.model.UserUi
import com.example.usersapp.ui.theme.AppTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import usersapp.composeapp.generated.resources.Res
import usersapp.composeapp.generated.resources.error_btn
import usersapp.composeapp.generated.resources.error_message
import usersapp.composeapp.generated.resources.users_title

@Composable
fun UsersScreen(viewModel: UserViewModel, onClick: (Int) -> Unit) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    when (val currentState = state.value) {
        UserUiState.Loading -> ScreenLoading()
        UserUiState.Error -> ScreenError(onTryAgain = {
            viewModel.onAction(
                UserAction.LoadUsers
            )
        })

        is UserUiState.Success -> ScreenSuccess(
            users = currentState.users,
            isLoading = currentState.loadingPage,
            onClick = onClick
        ) {
            viewModel.onAction(
                UserAction.LoadUsers
            )
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenSuccess(
    users: List<UserUi>,
    isLoading: Boolean,
    onClick: (Int) -> Unit,
    loadUsers: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { CenterAlignedTopAppBar(title = { Text(stringResource(Res.string.users_title)) }) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            items(
                count = users.size,
                key = { users[it].id },
            ) {
                val user = users[it]
                UserItem(
                    modifier = Modifier.padding(bottom = if (it == users.lastIndex) 24.dp else 0.dp)
                        .clickable {
                            onClick(user.id)
                        },
                    user = user
                )
            }
            item {
                LaunchedEffect(users) {
                    loadUsers()
                }
                if (isLoading)
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))

            }

        }
    }
}


@Composable
fun ScreenError(onTryAgain: () -> Unit) {
    Scaffold(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(Res.string.error_message))
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onTryAgain) { Text(stringResource(Res.string.error_btn)) }
        }
    }
}

@Composable
fun ScreenLoading() {
    Scaffold {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Preview
@Composable
private fun ScreenSuccessPreview() {

    AppTheme {
        ScreenSuccess(
            users = listOf(
                UserUi(1, "Vincenzo Sarni", ""),
                UserUi(2, "Luca Pocchione", ""),
                UserUi(3, "Mario Rossi", "")
            ),
            isLoading = true,
            loadUsers = {},
            onClick = {}
        )
    }
}