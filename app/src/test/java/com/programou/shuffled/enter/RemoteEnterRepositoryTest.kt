package com.programou.shuffled.enter

import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class RemoteEnterRepositoryTest {
    @Test
    fun `test on enter with calls client once after requests`() {
        val email = makeEmail()
        val password = makePassword()
        val enterRequest = makeRequest(email, password)
        val enter = Enter(email, password)
        val client = stubClientToReturns(StatusCode.OK.code)
        val sut = makeSUT(client)

        verify(client, times(0)).enter(enterRequest)

        sut.enter(enter)

        verify(client, times(1)).enter(enterRequest)
    }

    @Test
    fun `test on enter with OK status code (200) and no content in data returns NotFoundError failure`() {
        val email = makeEmail()
        val password = makePassword()
        val enter = Enter(email, password)
        val client = stubClientToReturns(StatusCode.OK.code)
        val sut = makeSUT(client)

        val result = sut.enter(enter)

        assertTrue(result.exceptionOrNull() is HTTPError.NotFoundError)
    }

    @Test
    fun `test on enter with OK status code (200) and content in data returns User model successfully`() {
        val enter = Enter(makeEmail(), makePassword())
        val userResponse = UserResponse.User("name", "email")
        val userModel = User(userResponse.name, userResponse.email)
        val client = stubClientToReturns(StatusCode.OK.code, userResponse)
        val sut = makeSUT(client)

        val result = sut.enter(enter)

        assertNull(result.exceptionOrNull())
        assertEquals(result.getOrNull(), userModel)
    }

    @Test
    fun `test on enter with not found status code (404) returns NotFoundError failure`() {
        val enter = Enter(makeEmail(), makePassword())
        val client = stubClientToReturns(StatusCode.NOT_FOUND_ERROR.code)
        val sut = makeSUT(client)

        val result = sut.enter(enter)

        assertTrue(result.exceptionOrNull() is HTTPError.NotFoundError)
    }

    @Test
    fun `test on enter with unauthorized status code (401) returns UnauthorizedError failure`() {
        val enter = Enter(makeEmail(), makePassword())
        val client = stubClientToReturns(StatusCode.UNAUTHORIZED_ERROR.code)
        val sut = makeSUT(client)

        val result = sut.enter(enter)

        assertTrue(result.exceptionOrNull() is HTTPError.UnauthorizedError)
    }

    @Test
    fun `test on enter with bad request status code (400) returns BadRequestError failure`() {
        val enter = Enter(makeEmail(), makePassword())
        val client = stubClientToReturns(StatusCode.BAD_REQUEST_ERROR.code)
        val sut = makeSUT(client)

        val result = sut.enter(enter)

        assertTrue(result.exceptionOrNull() is HTTPError.BadRequestError)
    }

    @Test
    fun `test on enter with internal server error status code (500) returns InternalServerError failure`() {
        val enter = Enter(makeEmail(), makePassword())
        val client = stubClientToReturns(StatusCode.INTERNAL_SERVER_ERROR.code)
        val sut = makeSUT(client)

        val result = sut.enter(enter)

        assertTrue(result.exceptionOrNull() is HTTPError.InternalServerError)
    }

    @Test
    fun `test on enter with unexpeceted error status code (500) returns UnexpectedError failure`() {
        val enter = Enter(makeEmail(), makePassword())
        val client = stubClientToReturns(UNEXPECTED_ERROR_STATUS_CODE)
        val sut = makeSUT(client)

        val result = sut.enter(enter)

        assertTrue(result.exceptionOrNull() is HTTPError.UnexpectedError)
    }

    private val UNEXPECTED_ERROR_STATUS_CODE = 300

    private fun stubClientToReturns(statusCode: Int, data: UserResponse.User? = null): EnterClientProvider = mock() {
        on { enter(any()) } doReturn (UserResponse(statusCode, data))
    }

    private fun makeEmail() = "any-user-email@email.com"

    private fun makePassword() = "any_password_account"

    private fun makeRequest(email: String, password: String): EnterRequest {
        val body = mapOf(
            Pair("email", email),
            Pair("password", password),
        )
        val method = "POST"
        return EnterRequest(method, body)
    }

    private fun makeSUT(client: EnterClientProvider) = RemoteEnterRepository(client)
}