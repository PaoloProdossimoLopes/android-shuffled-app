package com.programou.shuffled.unauthenticated.enter

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock

class RemoteEnterRepositoryTest {
    @Test
    fun `test on enter with calls client once after requests`() {
        val email = makeEmail()
        val password = makePassword()
        val enter = com.programou.shuffled.unauthenticated.enter.Enter(email, password)
        val client = stubClientToReturns(StatusCode.OK.code)
        val sut = makeSUT(client)

        val callbacksReceived = mutableListOf<Result<User>>()
        sut.enter(enter) { callbacksReceived.add(it) }

        assertEquals(callbacksReceived.size, 1)
    }

    @Test
    fun `test on enter with OK status code (200) and no content in data returns NotFoundError failure`() {
        val email = makeEmail()
        val password = makePassword()
        val enter = com.programou.shuffled.unauthenticated.enter.Enter(email, password)
        val client = stubClientToReturns(StatusCode.OK.code)
        val sut = makeSUT(client)

        val results = mutableListOf<Result<User>>()
        sut.enter(enter) { results.add(it) }

        assertEquals(results.count(), 1)
        assertTrue(results.firstOrNull()?.exceptionOrNull() is HTTPError.NotFoundError)
    }

    @Test
    fun `test on enter with OK status code (200) and content in data returns User model successfully`() {
        val enter = com.programou.shuffled.unauthenticated.enter.Enter(makeEmail(), makePassword())
        val userResponse = UserResponse.User("name", "email")
        val userModel = User(userResponse.name, userResponse.email)
        val client = stubClientToReturns(StatusCode.OK.code, userResponse)
        val sut = makeSUT(client)

        val results = mutableListOf<Result<User>>()
        sut.enter(enter) { results.add(it) }

        assertEquals(results.count(), 1)
        assertNull(results.firstOrNull()?.exceptionOrNull())
        assertEquals(results.firstOrNull()?.getOrNull(), userModel)
    }

    @Test
    fun `test on enter with not found status code (404) returns NotFoundError failure`() {
        val enter = com.programou.shuffled.unauthenticated.enter.Enter(makeEmail(), makePassword())
        val client = stubClientToReturns(StatusCode.NOT_FOUND_ERROR.code)
        val sut = makeSUT(client)

        val results = mutableListOf<Result<User>>()
        sut.enter(enter) { results.add(it) }

        assertEquals(results.count(), 1)
        assertTrue(results.firstOrNull()?.exceptionOrNull() is HTTPError.NotFoundError)
    }

    @Test
    fun `test on enter with unauthorized status code (401) returns UnauthorizedError failure`() {
        val enter = com.programou.shuffled.unauthenticated.enter.Enter(makeEmail(), makePassword())
        val client = stubClientToReturns(StatusCode.UNAUTHORIZED_ERROR.code)
        val sut = makeSUT(client)

        val results = mutableListOf<Result<User>>()
        sut.enter(enter) { results.add(it) }

        assertEquals(results.count(), 1)
        assertTrue(results.firstOrNull()?.exceptionOrNull() is HTTPError.UnauthorizedError)
    }

    @Test
    fun `test on enter with bad request status code (400) returns BadRequestError failure`() {
        val enter = com.programou.shuffled.unauthenticated.enter.Enter(makeEmail(), makePassword())
        val client = stubClientToReturns(StatusCode.BAD_REQUEST_ERROR.code)
        val sut = makeSUT(client)

        val results = mutableListOf<Result<User>>()
        sut.enter(enter) { results.add(it) }

        assertEquals(results.count(), 1)
        assertTrue(results.firstOrNull()?.exceptionOrNull() is HTTPError.BadRequestError)
    }

    @Test
    fun `test on enter with internal server error status code (500) returns InternalServerError failure`() {
        val enter = com.programou.shuffled.unauthenticated.enter.Enter(makeEmail(), makePassword())
        val client = stubClientToReturns(StatusCode.INTERNAL_SERVER_ERROR.code)
        val sut = makeSUT(client)

        val results = mutableListOf<Result<User>>()
        sut.enter(enter) { results.add(it) }

        assertEquals(results.count(), 1)
        assertTrue(results.firstOrNull()?.exceptionOrNull() is HTTPError.InternalServerError)
    }

    @Test
    fun `test on enter with unexpeceted error status code (500) returns UnexpectedError failure`() {
        val enter = com.programou.shuffled.unauthenticated.enter.Enter(makeEmail(), makePassword())
        val client = stubClientToReturns(UNEXPECTED_ERROR_STATUS_CODE)
        val sut = makeSUT(client)

        val results = mutableListOf<Result<User>>()
        sut.enter(enter) { results.add(it) }

        assertEquals(results.count(), 1)
        assertTrue(results.firstOrNull()?.exceptionOrNull() is HTTPError.UnexpectedError)
    }

    private val UNEXPECTED_ERROR_STATUS_CODE = 300

    private fun stubClientToReturns(statusCode: Int, data: UserResponse.User? = null): EnterClientProvider {
        val enterCallbackCapture = argumentCaptor<(UserResponse) -> Unit>()
        return mock() {
            on { enter(any(), enterCallbackCapture.capture()) } doAnswer  { enterCallbackCapture.firstValue(UserResponse(statusCode, data)) }
        }
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
