package com.example.usersapp.presentation.userdetails

import androidx.lifecycle.viewModelScope
import app.cash.turbine.test
import com.example.usersapp.domain.model.User
import com.example.usersapp.domain.usecase.GetUserDetailsUseCase
import com.example.usersapp.presentation.userdetails.model.toUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.kodein.mock.Mock
import org.kodein.mock.generated.injectMocks
import org.kodein.mock.tests.TestsWithMocks
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class UserDetailViewModelTest : TestsWithMocks() {

    @Mock
    lateinit var getUserDetailsUseCase: GetUserDetailsUseCase

    private val dummyUser = User(1, "test@example.com", "First", "Last", "avatar")

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var testScope: TestScope

    val viewModel by withMocks { UserDetailViewModel(1, getUserDetailsUseCase) }

    override fun setUpMocks() = mocker.injectMocks(this)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        testScope = TestScope(testDispatcher)
    }

    @Test
    fun `getState initial state is Loading`() = runTest {
        assertEquals(UserDetailUiState.Loading, viewModel.state.value)
    }

    @Test
    fun `getState emits Success on successful data fetch`() = runTest {
        every { getUserDetailsUseCase(1) } returns flowOf(dummyUser)

        viewModel.getUserDetails()

        viewModel.state.test {
            awaitItem() // Initial Loading
            val successState = awaitItem()
            assertEquals(UserDetailUiState.Success(dummyUser.toUi()), successState)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getState emits Error when use case throws an exception`() = runTest {
        every { getUserDetailsUseCase(1) } returns flow {
            throw RuntimeException("Network error")
        }

        viewModel.getUserDetails()

        viewModel.state.test {
            awaitItem() // Initial Loading
            val errorState = awaitItem()
            assertEquals(UserDetailUiState.Error, errorState)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getUserDetails calls use case with correct id`() = runTest {
        every { getUserDetailsUseCase(1) } returns flowOf(dummyUser)

        viewModel.getUserDetails()

        verifyWithSuspend { getUserDetailsUseCase(1) }
    }

    @Test
    fun `getUserDetails handles use case emission of multiple values  collectLatest behavior`() =
        runTest {
            every { getUserDetailsUseCase(1) } returns flow {
                emit(dummyUser.copy(firstName = "First1"))
                emit(dummyUser.copy(firstName = "First2"))
            }

            viewModel.getUserDetails()

            viewModel.state.test {
                awaitItem() // Initial Loading
                val latestState = awaitItem()
                assertEquals("First2", (latestState as UserDetailUiState.Success).user.name)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `getUserDetails job cancellation behavior`() = runTest {
        val flow: Flow<User> = flow {
            emit(dummyUser)
            kotlinx.coroutines.delay(Long.MAX_VALUE)
        }
        every { getUserDetailsUseCase(1) } returns flow

        val job = viewModel.getUserDetails()
        job.cancel()

        assertIs(job.isCancelled)
    }

    @Test
    fun `getUserDetails coroutine scope cancellation  viewModelScope cleared`() = runTest {
        val flow: Flow<User> = flow {
            emit(dummyUser)
            kotlinx.coroutines.delay(Long.MAX_VALUE)
        }
        every { getUserDetailsUseCase(1) } returns flow

        val job = viewModel.getUserDetails()
        viewModel.viewModelScope.cancel()

        assertIs(job.isCancelled || !job.isActive)
    }

}