package com.programou.shuffled.enter

interface EnterClientProvider {
    fun enter(request: EnterRequest): UserResponse
}

data class UserResponse(
    val statusCode: Int,
    val data: UserResponse.User?
) {
    data class User(
        val name: String,
        val email: String
    )
}

class UnauthorizedError: Error()
class BadRequestError: Error()
class UnexpectedError: Error()
class InternalServerError: Error()
class NotFoundError: Error()

data class EnterRequest(
    val method: String,
    val body: Map<String, String>
)

class RemoteEnterRepository(private val client: EnterClientProvider): EnterRepository {
    override fun enter(model: Enter): Result<User> {
        val method = "POST"
        val body = mapOf(
            Pair("email", model.email),
            Pair("password", model.password),
        )
        val request = EnterRequest(method, body)
        val response = client.enter(request)

        return when (response.statusCode) {
            400 -> Result.failure(BadRequestError())
            401 -> Result.failure(UnauthorizedError())
            500 -> Result.failure(InternalServerError())
            200 -> {
                response.data?.let {
                    return Result.success(User(it. name, it.email))
                }

                return Result.failure(NotFoundError())
            }
            404 -> Result.failure(NotFoundError())

            else -> Result.failure(UnexpectedError())
        }
    }
}