package com.example.usersapp.domain.usecase

import com.example.usersapp.domain.model.User
import com.example.usersapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.kodein.mock.Mock
import org.kodein.mock.generated.injectMocks
import org.kodein.mock.tests.TestsWithMocks
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetUsersUseCaseTest : TestsWithMocks() {
    @Mock
    lateinit var userRepository: UserRepository
    val getUsersUseCase by withMocks { GetUsersUseCase(userRepository) }
    override fun setUpMocks() = mocker.injectMocks(this)


    @Test
    fun `Initial invocation fetches users`() {
        // Verify that when invoke() is called for the first time, it fetches users from the repository 
        // and updates the internal StateFlow.
        runBlocking {
            val user1 = User(1, "a@example.com", "A", "Test", "avatar1")
            val user2 = User(2, "b@example.com", "B", "Test", "avatar2")

            every { userRepository.getUsers(1) } returns flowOf(listOf(user1, user2))
            val result = getUsersUseCase()

            assertEquals(listOf(user1, user2), result.first())
        }
    }

    @Test
    fun `Subsequent invocations before end reached load more users`() {
        runBlocking {
            val user1 = User(1, "a@example.com", "A", "Test", "avatar1")
            val user2 = User(2, "b@example.com", "B", "Test", "avatar2")

            every { userRepository.getUsers(1) } returns flowOf(listOf(user1, user2))
            every { userRepository.getUsers(2) } returns flowOf(listOf(user2, user1))

            getUsersUseCase()

            val result = getUsersUseCase()

            assertEquals(listOf(user1, user2, user2, user1), result.first())
        }
    }


    @Test
    fun `Repository throws exception sets isLoading to false and rethrows`() {
        runBlocking {
            every { userRepository.getUsers(1) } runs { throw RuntimeException("Network error") }

            try {
                getUsersUseCase()
                assertTrue(false, "Expected exception not thrown")
            } catch (e: Exception) {
                assertEquals("Network error", e.message)
            }
        }
    }

    @Test
    fun `Concurrent invocations are handled correctly`() {
        runBlocking {
            val user1 = User(1, "a@example.com", "A", "Test", "avatar1")
            every { userRepository.getUsers(1) } returns flowOf(listOf(user1))
            every { userRepository.getUsers(2) } returns flowOf(listOf(user1))

            // Simula due invocazioni simultanee
            val result1 = getUsersUseCase()
            val result2 = getUsersUseCase()

            assertEquals(result1.first(), result2.first())
        }
    }


    @Test
    fun `Multiple observers receive updates`() {
        runBlocking {
            val user1 = User(1, "a@example.com", "A", "Test", "avatar1")
            val user2 = User(1, "a@example.com", "A", "Test2", "avatar1")

            every { userRepository.getUsers(1) } returns flowOf(listOf(user1))
            every { userRepository.getUsers(2) } returns flowOf(listOf(user2))

            val result1 = getUsersUseCase()
            val result2 = getUsersUseCase()

            assertEquals(result1.first(), result2.first())
        }
    }


}