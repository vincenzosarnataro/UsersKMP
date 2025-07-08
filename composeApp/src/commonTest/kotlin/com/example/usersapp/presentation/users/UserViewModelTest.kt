package com.example.usersapp.presentation.users

import app.cash.turbine.test
import com.example.usersapp.domain.model.User
import com.example.usersapp.domain.usecase.GetUsersUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.kodein.mock.Mock
import org.kodein.mock.generated.injectMocks
import org.kodein.mock.tests.TestsWithMocks
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest : TestsWithMocks() {
    @Mock
    lateinit var getUsersUseCase: GetUsersUseCase
    val userViewModel by withMocks { UserViewModel(getUsersUseCase) }
    override fun setUpMocks() = mocker.injectMocks(this)

    private val sampleUsers = listOf(User(1, "a@example.com", "A", "Test", "avatar1"))

    @Test
    fun `Initial state is Loading`() = runTest {
        assertIs<UserUiState.Loading>(userViewModel.state.value)
    }

    @Test
    fun `State transitions to Success on successful user fetch  initial load`() = runTest {
        everySuspending { getUsersUseCase() } returns flow {
            emit(sampleUsers)
        }

        userViewModel.state.test {
            val first = awaitItem()
            assertIs<UserUiState.Loading>(first)

            val second = awaitItem()
            assertIs<UserUiState.Success>(second)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `State transitions to Error on failed user fetch  initial load`() = runTest {
        everySuspending { getUsersUseCase() } returns flow {
            throw RuntimeException("Error")
        }

        userViewModel.state.test {
            val first = awaitItem()
            assertIs<UserUiState.Loading>(first)

            val second = awaitItem()
            assertIs<UserUiState.Error>(second)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onAction LoadUsers  when current state is Loading`() = runTest {
        everySuspending { getUsersUseCase() } returns flow {
            emit(sampleUsers)
        }

        userViewModel.onAction(UserAction.LoadUsers)

        userViewModel.state.test {
            skipItems(1) // skip Loading
            val state = awaitItem()
            assertIs<UserUiState.Success>(state)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onAction LoadUsers  when current state is Success   successful fetch`() = runTest {
        everySuspending { getUsersUseCase() } returns flow {
            emit(sampleUsers)
        }

        userViewModel.onAction(UserAction.LoadUsers)
        userViewModel.state.test {
            skipItems(1)
            val successState = awaitItem()
            assertIs<UserUiState.Success>(successState)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getUsersUseCase returns an empty list`() = runTest {
        everySuspending { getUsersUseCase() } returns flow {
            emit(emptyList())
        }

        userViewModel.state.test {
            skipItems(1)
            val state = awaitItem()
            assertIs<UserUiState.Success>(state)
            assertEquals(0, (state as UserUiState.Success).users.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `users toUi   mapping correctness`() = runTest {
        everySuspending { getUsersUseCase() } returns flow {
            emit(sampleUsers)
        }

        userViewModel.state.test {
            skipItems(1)
            val state = awaitItem()
            val userUi = (state as UserUiState.Success).users.first()
            assertEquals("A Test", userUi.name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `StateFlow value replay for new subscribers`() = runTest {
        everySuspending { getUsersUseCase() } returns flow {
            emit(sampleUsers)
        }

        userViewModel.state.test {
            skipItems(1)
            val state = awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        userViewModel.state.test {
            val state = awaitItem()
            assertIs<UserUiState.Success>(state)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
