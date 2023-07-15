package com.programou.shuffled.register

import com.programou.shuffled.enter.HTTPError
import com.programou.shuffled.enter.StatusCode
import com.programou.shuffled.enter.User
import com.programou.shuffled.enter.UserResponse
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock

class RemoteRegisterRepositoryTest {
    @Test
    fun `test on register with calls client once after requests`() {
        val email = makeEmail()
        val password = makePassword()
        val register = Register(email, password)
        val client = stubClientToReturns(StatusCode.OK.code)
        val sut = makeSUT(client)

        val callbacksReceived = mutableListOf<Result<User>>()
        sut.register(register) { callbacksReceived.add(it) }

        Assert.assertEquals(callbacksReceived.size, 1)
    }

    @Test
    fun `test on register with OK status code (200) and no content in data returns NotFoundError failure`() {
        val email = makeEmail()
        val password = makePassword()
        val register = Register(email, password)
        val client = stubClientToReturns(StatusCode.OK.code)
        val sut = makeSUT(client)

        val results = mutableListOf<Result<User>>()
        sut.register(register) { results.add(it) }

        Assert.assertEquals(results.count(), 1)
        Assert.assertTrue(results.firstOrNull()?.exceptionOrNull() is HTTPError.NotFoundError)
    }

    @Test
    fun `test on register with OK status code (200) and content in data returns User model successfully`() {
        val register = Register(makeEmail(), makePassword())
        val userResponse = UserResponse.User("name", "email")
        val userModel = User(userResponse.name, userResponse.email)
        val client = stubClientToReturns(StatusCode.OK.code, userResponse)
        val sut = makeSUT(client)

        val results = mutableListOf<Result<User>>()
        sut.register(register) { results.add(it) }

        Assert.assertEquals(results.count(), 1)
        Assert.assertNull(results.firstOrNull()?.exceptionOrNull())
        Assert.assertEquals(results.firstOrNull()?.getOrNull(), userModel)
    }

    @Test
    fun `test on register with not found status code (404) returns NotFoundError failure`() {
        val register = Register(makeEmail(), makePassword())
        val client = stubClientToReturns(StatusCode.NOT_FOUND_ERROR.code)
        val sut = makeSUT(client)

        val results = mutableListOf<Result<User>>()
        sut.register(register) { results.add(it) }

        Assert.assertEquals(results.count(), 1)
        Assert.assertTrue(results.firstOrNull()?.exceptionOrNull() is HTTPError.NotFoundError)
    }

    @Test
    fun `test on register with unauthorized status code (401) returns UnauthorizedError failure`() {
        val register = Register(makeEmail(), makePassword())
        val client = stubClientToReturns(StatusCode.UNAUTHORIZED_ERROR.code)
        val sut = makeSUT(client)

        val results = mutableListOf<Result<User>>()
        sut.register(register) { results.add(it) }

        Assert.assertEquals(results.count(), 1)
        Assert.assertTrue(results.firstOrNull()?.exceptionOrNull() is HTTPError.UnauthorizedError)
    }

    @Test
    fun `test on register with bad request status code (400) returns BadRequestError failure`() {
        val register = Register(makeEmail(), makePassword())
        val client = stubClientToReturns(StatusCode.BAD_REQUEST_ERROR.code)
        val sut = makeSUT(client)

        val results = mutableListOf<Result<User>>()
        sut.register(register) { results.add(it) }

        Assert.assertEquals(results.count(), 1)
        Assert.assertTrue(results.firstOrNull()?.exceptionOrNull() is HTTPError.BadRequestError)
    }

    @Test
    fun `test on register with internal server error status code (500) returns InternalServerError failure`() {
        val register = Register(makeEmail(), makePassword())
        val client = stubClientToReturns(StatusCode.INTERNAL_SERVER_ERROR.code)
        val sut = makeSUT(client)

        val results = mutableListOf<Result<User>>()
        sut.register(register) { results.add(it) }

        Assert.assertEquals(results.count(), 1)
        Assert.assertTrue(results.firstOrNull()?.exceptionOrNull() is HTTPError.InternalServerError)
    }

    @Test
    fun `test on register with unexpeceted error status code (500) returns UnexpectedError failure`() {
        val register = Register(makeEmail(), makePassword())
        val client = stubClientToReturns(UNEXPECTED_ERROR_STATUS_CODE)
        val sut = makeSUT(client)

        val results = mutableListOf<Result<User>>()
        sut.register(register) { results.add(it) }

        Assert.assertEquals(results.count(), 1)
        Assert.assertTrue(results.firstOrNull()?.exceptionOrNull() is HTTPError.UnexpectedError)
    }

    private val UNEXPECTED_ERROR_STATUS_CODE = 300

    private fun stubClientToReturns(statusCode: Int, data: UserResponse.User? = null): RegisterClientProvider {
        val enterCallbackCapture = argumentCaptor<(UserResponse) -> Unit>()
        return mock() {
            on { register(any(), enterCallbackCapture.capture()) } doAnswer  { enterCallbackCapture.firstValue(
                UserResponse(statusCode, data)
            ) }
        }
    }

    private fun makeEmail() = "any-user-email@email.com"

    private fun makePassword() = "any_password_account"

    private fun makeSUT(client: RegisterClientProvider) = RemoteRegisterRepository(client)
}