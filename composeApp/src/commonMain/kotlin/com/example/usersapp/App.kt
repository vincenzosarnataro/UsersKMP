package com.example.usersapp

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.usersapp.presentation.userdetails.UserDetailScreen
import com.example.usersapp.presentation.userdetails.UserDetailViewModel
import com.example.usersapp.presentation.users.UserViewModel
import com.example.usersapp.presentation.users.UsersScreen
import com.example.usersapp.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
@Preview
fun App() {
    val navController = rememberNavController()
    AppTheme {
        NavHost(navController = navController, startDestination = Routing.Start) {
            composable<Routing.Start> {
                val viewModel = koinViewModel<UserViewModel>()
                UsersScreen(viewModel = viewModel, onClick = {
                    navController.navigate(Routing.Detail(it))
                })
            }
            composable<Routing.Detail> { backStackEntry ->
                val detail: Routing.Detail = backStackEntry.toRoute()
                val viewModel =
                    koinInject<UserDetailViewModel>(parameters = { parametersOf(detail.id) })
                UserDetailScreen(viewModel = viewModel, onBack = {
                    navController.popBackStack()
                })
            }
        }


    }
}

