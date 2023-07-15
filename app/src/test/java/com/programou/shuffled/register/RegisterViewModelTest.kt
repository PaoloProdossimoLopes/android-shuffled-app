package com.programou.shuffled.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.programou.shuffled.enter.ErrorViewData
import com.programou.shuffled.enter.User
import com.programou.shuffled.enter.UserViewData
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class RegisterViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val userObserver: Observer<User> = mock()
    private val errorMessageObserver: Observer<ErrorViewData> = mock()

    @Test
    fun `on enter no emit any user for user data observer`() {
        val sut = makeSUT(mock())
        sut.errorMessage.observeForever(errorMessageObserver)

        sut.register(makeViewData())

        verify(errorMessageObserver, times(0)).onChanged(any())
    }

    @Test
    fun `on enter when use case returns a failure result deleivers a ErrorViewModel as returned`() {
        val viewData = makeViewData()
        val errorMessage = "any error message delievery"
        val useCase = stubUseCaseOnEnterWith(Result.failure(Error(errorMessage)), viewData)
        val sut = makeSUT(useCase)
        sut.errorMessage.observeForever(errorMessageObserver)

        sut.register(viewData)

        verify(errorMessageObserver, times(1)).onChanged(ErrorViewData(errorMessage))
    }

    @Test
    fun `on enter when use case returns nullable result no emits for error observer`() {
        val sut = makeSUT(mock())
        sut.user.observeForever(userObserver)

        sut.register(makeViewData())

        verify(userObserver, times(0)).onChanged(any())
    }

    @Test
    fun `on enter with usecase completion succefully emit a user`() {
        val viewData = makeViewData()
        val result = Result.success(makeUser(viewData))
        val useCase = stubUseCaseOnEnterWith(result, viewData)
        val sut = makeSUT(useCase)

        sut.user.observeForever(userObserver)
        sut.errorMessage.observeForever(errorMessageObserver)

        sut.register(viewData)

        verify(errorMessageObserver, times(0)).onChanged(any())
        verify(userObserver, times(1)).onChanged(any())
    }

    private fun makeViewData() = UserViewData("any email", "any password")

    private fun makeSUT(useCase: RegisterAccount) = RegisterViewModel(useCase)

    private fun stubUseCaseOnEnterWith(result: Result<User>, viewData: UserViewData): RegisterAccount {
        val callbackCapture = argumentCaptor<(Result<User>) -> Unit>()
        return mock {
            on {
                register(eq(Register(viewData.email, viewData.password)), callbackCapture.capture())
            } doAnswer {
                callbackCapture.firstValue(result)
            }
        }
    }

    private fun makeUser(viewData: UserViewData) = User("any user name", viewData.email)
}
