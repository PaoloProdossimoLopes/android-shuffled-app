package com.programou.shuffled.enter

import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class EnterAccountUseCaseTest {

    @Test
    fun `on enter with any data but receive a null retuns an failure`() {
        val sut = makeSUT(mock())

        val enterResult = sut.enter(makeEnterModel())

        assertTrue(enterResult.isFailure)
    }

    @Test
    fun `on enter with any data but receive a null returns unexpeceted error message`() {
        val sut = makeSUT(mock())

        val enterResult = sut.enter(makeEnterModel())

        assertEquals(enterResult.exceptionOrNull()?.message, "Ops, algo inesperado ocorreu, tente novamente mais tarde")
    }

    @Test
    fun `on enter with invalid email returns an failure`() {
        val enterModel = makeEnterModel(email = "invalid email")
        val env = makeEnviroment(repositoryShouldReturns = Result.failure(makeError()))

        val enterResult = env.sut.enter(enterModel)

        assertTrue(enterResult.isFailure)
    }

    @Test
    fun `on enter with invalid password returns an failure`() {
        val enterModel = makeEnterModel(password = "invalid password")
        val env = makeEnviroment(repositoryShouldReturns = Result.failure(makeError()))

        val enterResult = env.sut.enter(enterModel)

        assertTrue(enterResult.isFailure)
    }

    @Test
    fun `on enter with invalid password returns message saying possible user or password was wrong`() {
        val enterModel = makeEnterModel(password = "invalid password")
        val env = makeEnviroment(repositoryShouldReturns = Result.failure(makeError()))

        val enterResult = env.sut.enter(enterModel)

        assertEquals(enterResult.exceptionOrNull()?.message, "Dados inválidos. Email ou senha estao incorretos.")
    }

    @Test
    fun `on enter with invalid email returns message saying possible user or password was wrong`() {
        val enterModel = makeEnterModel(email = "invalid email")
        val env = makeEnviroment(repositoryShouldReturns = Result.failure(makeError()))

        val enterResult = env.sut.enter(enterModel)

        assertEquals(enterResult.exceptionOrNull()?.message, "Dados inválidos. Email ou senha estao incorretos.")
    }

    private fun makeError() = Error("deleivers an error")

    @Test
    fun `on enter with valid credentials retuns an success result`() {
        val enterModel = makeEnterModel()
        val repositoryReturns = Result.success(enterModel.toUser())
        val env = makeEnviroment(repositoryReturns)

        val enterResult = env.sut.enter(enterModel)

        assertTrue(enterResult.isSuccess)
    }

    @Test
    fun `on enter with valid credentials retuns informations provided by repositorory`() {
        val enterModel = makeEnterModel()
        val userModel = User("any name", enterModel.email)
        val repositoryReturns = Result.success(userModel)
        val env = makeEnviroment(repositoryReturns)

        val enterResult = env.sut.enter(enterModel)

        assertEquals(enterResult.getOrNull(), userModel)
    }

    private data class Environment(val sut: EnterAccountUseCase, val repository: EnterRepository)

    private fun makeEnviroment(repositoryShouldReturns: Result<User>): Environment {
        val repository = makeRepositoryMock(repositoryShouldReturns)
        val sut = makeSUT(repository)
        return Environment(sut, repository)
    }

    private fun makeSUT(repository: EnterRepository) = EnterAccountUseCase(repository)

    private fun makeRepositoryMock(shouldReturn: Result<User>?): EnterRepository {
        shouldReturn?.let {
            return mock { on { enter(any()) } doReturn (shouldReturn) }
        }

        return mock()
    }

    private fun makeEnterModel(
        email: String = "any valid email",
        password: String = "any valid password"
    ) = Enter(email, password)

    private fun Enter.toUser() = User(
        "any name delievered by repository",
        "any email delievered by repository"
    )
}