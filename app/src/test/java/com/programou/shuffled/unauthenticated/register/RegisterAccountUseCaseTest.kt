package com.programou.shuffled.unauthenticated.register


import com.programou.shuffled.unauthenticated.enter.User
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock

class EnterAccountUseCaseTest {

    @Test
    fun `on register with any data but receive a null retuns an failure`() {
        val result = Result.failure<User>(Error())
        val repository = makeRepositoryMock(result)
        val sut = makeSUT(repository)

        val results: MutableList<Result<User>> = mutableListOf()
        sut.register(makeEnterModel()) { result ->  results.add(result) }

        Assert.assertEquals(results.count(), 1)
        Assert.assertTrue(results.firstOrNull()?.isFailure == true)
    }

    @Test
    fun `on register with invalid email returns an failure`() {
        val enterModel = makeEnterModel(email = "invalid email")
        val env = makeEnviroment(repositoryShouldReturns = Result.failure(makeError()))

        val results: MutableList<Result<User>> = mutableListOf()
        env.sut.register(enterModel) { result -> results.add(result) }

        Assert.assertEquals(results.count(), 1)
        Assert.assertTrue(results.firstOrNull()?.isFailure == true)
    }

    @Test
    fun `on register with invalid password returns an failure`() {
        val enterModel = makeEnterModel(password = "invalid password")
        val env = makeEnviroment(repositoryShouldReturns = Result.failure(makeError()))

        val results: MutableList<Result<User>> = mutableListOf()
        env.sut.register(enterModel) { result -> results.add(result) }

        Assert.assertEquals(results.count(), 1)
        Assert.assertTrue(results.firstOrNull()?.isFailure == true)
    }

    @Test
    fun `on register with invalid password returns message saying possible user or password was wrong`() {
        val enterModel = makeEnterModel(password = "invalid password")
        val env = makeEnviroment(repositoryShouldReturns = Result.failure(makeError()))

        val results: MutableList<Result<User>> = mutableListOf()
        env.sut.register(enterModel) { result -> results.add(result) }

        Assert.assertEquals(results.count(), 1)
        Assert.assertEquals(
            results.firstOrNull()?.exceptionOrNull()?.message,
            "Dados inválidos. Email ou senha estao incorretos."
        )
    }

    @Test
    fun `on register with invalid email returns message saying possible user or password was wrong`() {
        val enterModel = makeEnterModel(email = "invalid email")
        val env = makeEnviroment(repositoryShouldReturns = Result.failure(makeError()))

        val results: MutableList<Result<User>> = mutableListOf()
        env.sut.register(enterModel) { result -> results.add(result) }

        Assert.assertEquals(results.count(), 1)
        Assert.assertEquals(
            results.firstOrNull()?.exceptionOrNull()?.message,
            "Dados inválidos. Email ou senha estao incorretos."
        )
    }

    private fun makeError() = Error("deleivers an error")

    @Test
    fun `on register with valid credentials retuns an success result`() {
        val enterModel = makeEnterModel()
        val repositoryReturns = Result.success(enterModel.toUser())
        val env = makeEnviroment(repositoryReturns)

        val results: MutableList<Result<User>> = mutableListOf()
        env.sut.register(enterModel) { result -> results.add(result) }

        Assert.assertEquals(results.count(), 1)
        Assert.assertTrue(results.firstOrNull()?.isSuccess == true)
    }

    @Test
    fun `on register with valid credentials retuns informations provided by repositorory`() {
        val enterModel = makeEnterModel()
        val userModel = User("any name", enterModel.email)
        val repositoryReturns = Result.success(userModel)
        val env = makeEnviroment(repositoryReturns)

        val results: MutableList<Result<User>> = mutableListOf()
        env.sut.register(enterModel) { result -> results.add(result) }

        Assert.assertEquals(results.count(), 1)
        Assert.assertEquals(results.firstOrNull()?.getOrNull(), userModel)
    }

    private data class Environment(val sut: RegisterAccountUseCase, val repository: RegisterRepository)

    private fun makeEnviroment(repositoryShouldReturns: Result<User>): Environment {
        val repository = makeRepositoryMock(repositoryShouldReturns)
        val sut = makeSUT(repository)
        return Environment(sut, repository)
    }

    private fun makeSUT(repository: RegisterRepository) = RegisterAccountUseCase(repository)

    private fun makeRepositoryMock(shouldReturn: Result<User>? = null): RegisterRepository {
        val enterCallback = argumentCaptor<(Result<User>) -> Unit>()

        shouldReturn?.let {
            return mock() {
                on { register(any(), enterCallback.capture()) } doAnswer { enterCallback.firstValue(shouldReturn) }
            }
        }

        return mock()
    }

    private fun makeEnterModel(
        email: String = "any valid email",
        password: String = "any valid password"
    ) = Register(email, password)

    private fun Register.toUser() = User(
        "any name delievered by repository",
        "any email delievered by repository"
    )
}