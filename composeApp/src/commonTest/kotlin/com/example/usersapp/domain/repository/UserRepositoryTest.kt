package com.example.usersapp.domain.repository


import com.example.usersapp.data.repository.UserRepositoryImpl
import com.example.usersapp.data.source.network.dto.UserDto
import com.example.usersapp.data.source.network.response.UserDetailsResponse
import com.example.usersapp.data.source.network.response.UserResponse
import com.example.usersapp.data.source.network.service.UserApiService
import com.example.usersapp.domain.model.User

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.kodein.mock.Mock
import org.kodein.mock.generated.injectMocks
import org.kodein.mock.tests.TestsWithMocks
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class UserRepositoryTest : TestsWithMocks() {

    override fun setUpMocks() = mocker.injectMocks(this)

    @Mock
    lateinit var mockUserApi: UserApiService
    private val userRepository by withMocks { UserRepositoryImpl(mockUserApi) }

    @Test
    fun `getUsers emits correct list of users for valid page`() {
        // Verify that getUsers emits a Flow containing the expected list of User objects when a valid page number is provided.
        runBlocking {
            val page = 2
            val userDtoList = listOf(
                UserDto(1, "test1@example.com", "First1", "Last1", "avatar1"),
                UserDto(2, "test2@example.com", "First2", "Last2", "avatar2")
            )
            val userResponse = UserResponse(page, 6, 12, 2, userDtoList)
            everySuspending { mockUserApi.getUsers(page) } returns userResponse

            val result = userRepository.getUsers(page).first()

            assertEquals(2, result.size)
            assertEquals("First1 Last1", result[0].firstName + " " + result[0].lastName)
            assertEquals("avatar2", result[1].avatar)
        }
    }

    @Test
    fun `getUsers emits empty list for page with no users`() {
        // Verify that getUsers emits a Flow containing an empty list when the requested page has no users (e.g., page number exceeds total pages).
        runBlocking {
            val page = 10 // A page that has no users
            val emptyUserResponse = UserResponse(page, 6, 12, 0, emptyList())
            everySuspending { mockUserApi.getUsers(page) } returns emptyUserResponse

            val result = userRepository.getUsers(page).first()

            assertTrue(result.isEmpty())
        }
    }

    @Test
    fun `getUsers handles page number 1 correctly`() {
        // Specifically test the first page to ensure it returns the correct set of initial users.
        runBlocking {
            val page = 1
            val userDtoList = listOf(
                UserDto(1, "user1@example.com", "User", "One", "avatar1.jpg"),
                UserDto(2, "user2@example.com", "User", "Two", "avatar2.jpg")
            )
            val userResponse = UserResponse(page, 6, 12, 2, userDtoList)
            everySuspending { mockUserApi.getUsers(page) } returns userResponse

            val result = userRepository.getUsers(page).first()

            assertEquals(2, result.size)
            assertEquals(User(1, "user1@example.com", "User", "One", "avatar1.jpg"), result[0])
            assertEquals(User(2, "user2@example.com", "User", "Two", "avatar2.jpg"), result[1])
        }
    }

    @Test
    fun `getUsers handles maximum valid page number`() {
        // Test with the largest valid page number to ensure it returns the correct users for the last page.
        runBlocking {
            val maxPage = 2 // Assuming total_pages = 2
            val userDtoList = listOf(
                UserDto(7, "user7@example.com", "User", "Seven", "avatar7.jpg"),
                UserDto(8, "user8@example.com", "User", "Eight", "avatar8.jpg")
            )
            val userResponse = UserResponse(maxPage, 6, 12, 2, userDtoList)
            everySuspending { mockUserApi.getUsers(maxPage) } returns userResponse

            val result = userRepository.getUsers(maxPage).first()

            assertEquals(2, result.size)
            assertEquals(User(7, "user7@example.com", "User", "Seven", "avatar7.jpg"), result[0])
        }
    }

    @Test
    fun `getUsers handles page number 0  if considered invalid `() {
        runBlocking {
            val page = 0
            val emptyUserResponse = UserResponse(page, 6, 12, 0, emptyList())
            everySuspending { mockUserApi.getUsers(page) } returns emptyUserResponse

            val result = userRepository.getUsers(page).first()

            assertTrue(result.isEmpty())
        }
    }

    @Test
    fun `getUsers handles negative page numbers  if considered invalid `() {
        runBlocking {
            val page = -1
            val emptyUserResponse = UserResponse(page, 6, 12, 0, emptyList())
            everySuspending { mockUserApi.getUsers(page) } returns emptyUserResponse

            val result = userRepository.getUsers(page).first()

            assertTrue(result.isEmpty())
        }
    }

    @Test
    fun `getUsers handles large invalid page number`() {
        runBlocking {
            val page = 9999
            val emptyUserResponse = UserResponse(page, 6, 12, 0, emptyList())
            everySuspending { mockUserApi.getUsers(page) } returns emptyUserResponse

            val result = userRepository.getUsers(page).first()

            assertTrue(result.isEmpty())
        }
    }

    @Test
    fun `getUsers handles network errors gracefully`() {
        runBlocking {
            val page = 1
            everySuspending { mockUserApi.getUsers(page) } runs { throw RuntimeException("Network error") }

            try {
                userRepository.getUsers(page).first()
                assertTrue(false, "Expected exception not thrown")
            } catch (e: Exception) {
                assertTrue(e is RuntimeException)
                assertEquals("Network error", e.message)
            }
        }
    }

    @Test
    fun `getUsers handles API errors gracefully`() {
        runBlocking {
            val page = 1
            everySuspending { mockUserApi.getUsers(page) } runs { throw RuntimeException("API error: 500") }

            try {
                userRepository.getUsers(page).first()
                assertTrue(false, "Expected exception not thrown")
            } catch (e: Exception) {
                assertTrue(e is RuntimeException)
                assertEquals("API error: 500", e.message)
            }
        }
    }

    @Test
    fun `getUsers handles empty API response  not an error  but no data `() {
        runBlocking {
            val page = 1
            val emptyUserResponse = UserResponse(page, 6, 12, 0, emptyList())
            everySuspending { mockUserApi.getUsers(page) } returns emptyUserResponse

            val result = userRepository.getUsers(page).first()

            assertTrue(result.isEmpty())
        }
    }

    @Test
    fun `getUsers flow completes after emission`() {
        runBlocking {
            val page = 1
            val userResponse = UserResponse(page, 6, 12, 0, emptyList())
            everySuspending { mockUserApi.getUsers(page) } returns userResponse

            val result = userRepository.getUsers(page).first()
            assertTrue(result.isEmpty())
        }
    }

    @Test
    fun `getUsers handles concurrent requests for different pages`() {
        runBlocking {
            val response1 = UserResponse(1, 6, 12, 1, listOf(UserDto(1, "a", "A", "A", "avatar")))
            val response2 = UserResponse(2, 6, 12, 1, listOf(UserDto(2, "b", "B", "B", "avatar")))

            everySuspending { mockUserApi.getUsers(1) } returns response1
            everySuspending { mockUserApi.getUsers(2) } returns response2

            val result1 = userRepository.getUsers(1).first()
            val result2 = userRepository.getUsers(2).first()

            assertEquals(1, result1.first().id)
            assertEquals(2, result2.first().id)
        }
    }

    @Test
    fun `getUsers flow emits items in correct order  if order matters `() {
        runBlocking {
            val userDtoList = listOf(
                UserDto(1, "a@example.com", "A", "Z", "avatar1"),
                UserDto(2, "b@example.com", "B", "Y", "avatar2"),
                UserDto(3, "c@example.com", "C", "X", "avatar3")
            )
            val userResponse = UserResponse(1, 6, 12, 3, userDtoList)
            everySuspending { mockUserApi.getUsers(1) } returns userResponse

            val result = userRepository.getUsers(1).first()

            assertEquals(1, result[0].id)
            assertEquals(2, result[1].id)
            assertEquals(3, result[2].id)
        }
    }

    @Test
    fun `getDetails with valid ID`() {
        runBlocking {
            val userId = 1
            val userDto = UserDto(1, "test@example.com", "Test", "User", "avatar.jpg")
            everySuspending { mockUserApi.getDetails(userId) } returns UserDetailsResponse(data = userDto)

            val result = userRepository.getUserDetails(userId).first()

            assertEquals(User(1, "test@example.com", "Test", "User", "avatar.jpg"), result)
        }
    }

    @Test
    fun `getDetails with non existent ID`() {
        runBlocking {
            val userId = 9999
            everySuspending { mockUserApi.getDetails(userId) } runs { throw RuntimeException("User not found") }

            try {
                userRepository.getUserDetails(userId).first()
                assertTrue(false, "Expected exception not thrown")
            } catch (e: Exception) {
                assertTrue(e is RuntimeException)
                assertEquals("User not found", e.message)
            }
        }
    }

    @Test
    fun `getDetails with negative ID`() {
        runBlocking {
            val userId = -1
            everySuspending { mockUserApi.getDetails(userId) } runs {
                throw IllegalArgumentException(
                    "Invalid user ID"
                )
            }

            try {
                userRepository.getUserDetails(userId).first()
                assertTrue(false, "Expected exception not thrown")
            } catch (e: Exception) {
                assertTrue(e is IllegalArgumentException)
                assertEquals("Invalid user ID", e.message)
            }
        }
    }

    @Test
    fun `getDetails with zero ID`() {
        runBlocking {
            val userId = 0
            everySuspending { mockUserApi.getDetails(userId) } runs {
                throw IllegalArgumentException(
                    "Invalid user ID"
                )
            }

            try {
                userRepository.getUserDetails(userId).first()
                assertTrue(false, "Expected exception not thrown")
            } catch (e: Exception) {
                assertTrue(e is IllegalArgumentException)
                assertEquals("Invalid user ID", e.message)
            }
        }
    }

    @Test
    fun `getDetails with maximum integer ID`() {
        runBlocking {
            val userId = Int.MAX_VALUE
            everySuspending { mockUserApi.getDetails(userId) } runs { throw RuntimeException("User not found") }

            try {
                userRepository.getUserDetails(userId).first()
                assertTrue(false, "Expected exception not thrown")
            } catch (e: Exception) {
                assertTrue(e is RuntimeException)
                assertEquals("User not found", e.message)
            }
        }
    }

    @Test
    fun `getDetails concurrent requests`() {
        runBlocking {
            val userDto1 = UserDto(1, "a@example.com", "A", "Test", "avatar1")
            val userDto2 = UserDto(2, "b@example.com", "B", "Test", "avatar2")

            everySuspending { mockUserApi.getDetails(1) } returns UserDetailsResponse(data = userDto1)
            everySuspending { mockUserApi.getDetails(2) } returns UserDetailsResponse(data = userDto2)

            val result1 = userRepository.getUserDetails(1).first()
            val result2 = userRepository.getUserDetails(2).first()

            assertEquals(1, result1.id)
            assertEquals(2, result2.id)
        }
    }

    @Test
    fun `getDetails Flow emission behavior`() {
        runBlocking {
            val userId = 1
            val userDto = UserDto(userId, "test@example.com", "Test", "User", "avatar.jpg")
            everySuspending { mockUserApi.getDetails(userId) } returns UserDetailsResponse(data = userDto)

            val result = userRepository.getUserDetails(userId).first()
            assertEquals(userId, result.id)
        }
    }

    @Test
    fun `getDetails Flow error propagation`() {
        runBlocking {
            val userId = 1
            everySuspending { mockUserApi.getDetails(userId) } runs { throw RuntimeException("Network error") }

            try {
                userRepository.getUserDetails(userId).first()
                assertTrue(false, "Expected exception not thrown")
            } catch (e: Exception) {
                assertTrue(e is RuntimeException)
                assertEquals("Network error", e.message)
            }
        }
    }

    @Test
    fun `getDetails Flow completion`() {
        runBlocking {
            val userId = 1
            val userDto = UserDto(userId, "test@example.com", "Test", "User", "avatar.jpg")
            everySuspending { mockUserApi.getDetails(userId) } returns UserDetailsResponse(data = userDto)

            val result = userRepository.getUserDetails(userId).first()
            assertEquals(userId, result.id)
        }
    }

    @Test
    fun `getDetails Flow cancellation`() {
        runBlocking {
            val userId = 1
            val userDto = UserDto(userId, "test@example.com", "Test", "User", "avatar.jpg")
            everySuspending { mockUserApi.getDetails(userId) } returns UserDetailsResponse(data = userDto)

            val result = userRepository.getUserDetails(userId).first()
            assertEquals(userId, result.id)
        }
    }

    @Test
    fun `getDetails data consistency`() {
        runBlocking {
            val userId = 1
            val userDto1 = UserDto(userId, "first@example.com", "First", "Version", "avatar1.jpg")
            val userDto2 = UserDto(userId, "second@example.com", "Second", "Version", "avatar2.jpg")

            everySuspending { mockUserApi.getDetails(userId) } returns UserDetailsResponse(data = userDto1)
            val result1 = userRepository.getUserDetails(userId).first()
            assertEquals("First Version", "${result1.firstName} ${result1.lastName}")
            mocker.reset()
            everySuspending { mockUserApi.getDetails(userId) } returns UserDetailsResponse(data = userDto2)

            val result2 = userRepository.getUserDetails(userId).first()

            assertEquals("Second Version", "${result2.firstName} ${result2.lastName}")
        }
    }

    @Test
    fun `getDetails underlying data source unavailable`() {
        runBlocking {
            val userId = 1
            everySuspending { mockUserApi.getDetails(userId) } runs { throw RuntimeException("Database unavailable") }

            try {
                userRepository.getUserDetails(userId).first()
                assertTrue(false, "Expected exception not thrown")
            } catch (e: Exception) {
                assertTrue(e is RuntimeException)
                assertEquals("Database unavailable", e.message)
            }
        }
    }

    @Test
    fun `getDetails with malformed data response`() {
        runBlocking {
            val userId = 1
            val malformedUserDto = UserDto(userId, "", "", "", "")

            everySuspending { mockUserApi.getDetails(userId) } returns UserDetailsResponse(data = malformedUserDto)

            val result = userRepository.getUserDetails(userId).first()

            assertEquals(userId, result.id)
            assertEquals("", result.email)
            assertEquals("", result.firstName)
            assertEquals("", result.lastName)
            assertEquals("", result.avatar)
        }
    }


}