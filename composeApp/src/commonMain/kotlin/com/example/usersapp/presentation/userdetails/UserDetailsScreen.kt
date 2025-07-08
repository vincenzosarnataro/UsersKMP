package com.example.usersapp.presentation.userdetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.SubcomposeAsyncImage
import com.example.usersapp.presentation.userdetails.model.UserDetailUi
import com.example.usersapp.ui.theme.AppTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import usersapp.composeapp.generated.resources.Res
import usersapp.composeapp.generated.resources.error_btn
import usersapp.composeapp.generated.resources.error_message
import usersapp.composeapp.generated.resources.user_action1
import usersapp.composeapp.generated.resources.user_action2
import usersapp.composeapp.generated.resources.user_action3
import usersapp.composeapp.generated.resources.user_info

@Composable
fun UserDetailScreen(viewModel: UserDetailViewModel, onBack: () -> Unit) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    when (val currentState = state.value) {
        UserDetailUiState.Loading -> ScreenLoading()
        UserDetailUiState.Error -> ScreenError(onTryAgain = viewModel::getUserDetails)
        is UserDetailUiState.Success -> ContactDetailScreen(currentState.user, onBack)

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailScreen(user: UserDetailUi, onBack: () -> Unit) {
    Scaffold(topBar = {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
        )
    }) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(16.dp),
        ) {
            SubcomposeAsyncImage(
                modifier = Modifier.size(128.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                model = user.avatar,
                contentDescription = "avatar",
                error = {
                    Image(
                        modifier = Modifier.size(128.dp)
                            .clip(CircleShape),
                        imageVector = Icons.Filled.Person,
                        contentDescription = "avatar"
                    )
                }

            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = user.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(
                    icon = Icons.Default.Call,
                    label = stringResource(Res.string.user_action1)
                )
                ActionButton(
                    icon = Icons.AutoMirrored.Default.Message,
                    label = stringResource(Res.string.user_action2)
                )
                ActionButton(
                    icon = Icons.Default.Videocam,
                    label = stringResource(Res.string.user_action3)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    Modifier.padding(start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Outlined.Email, contentDescription = "email")
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(user.email, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            stringResource(Res.string.user_info),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


        }
    }
}


@Composable
fun ActionButton(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FloatingActionButton(
            onClick = {},
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
        ) {
            Icon(icon, contentDescription = label)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            label, style = MaterialTheme.typography.bodySmall
        )
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

@Preview
@Composable
fun ContactDetailScreenPreview() {
    AppTheme {
        ContactDetailScreen(
            user = UserDetailUi(1, "Vincenzo Sarni", "", email = "test@test"),
            onBack = {})
    }
}