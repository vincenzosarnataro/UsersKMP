package com.example.usersapp.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.SubcomposeAsyncImage
import com.example.usersapp.presentation.users.model.UserUi
import com.example.usersapp.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun UserItem(modifier: Modifier = Modifier, user: UserUi) {

    ListItem(
        modifier = modifier,
        leadingContent = {
            SubcomposeAsyncImage(
                modifier = Modifier.size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                model = user.avatar,
                contentDescription = "avatar",
                error = {
                    Image(
                        modifier = Modifier.size(64.dp)
                            .clip(CircleShape),
                        imageVector = Icons.Filled.Person,
                        contentDescription = "avatar"
                    )
                }

            )
        },
        headlineContent = {
            Text(user.name)
        })
}

@OptIn(ExperimentalCoilApi::class)
@Preview
@Composable
private fun UserItemPreview() {

    AppTheme {
        UserItem(user = UserUi(1, "Vincenzo Sarni", avatar = ""))


    }
}