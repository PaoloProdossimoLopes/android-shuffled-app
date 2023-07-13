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

class HTTPError {
    class UnauthorizedError : Error()
    class BadRequestError : Error()
    class UnexpectedError : Error()
    class InternalServerError : Error()
    class NotFoundError : Error()
}

data class EnterRequest(
    val method: String,
    val body: Map<String, String>
)

enum class StatusCode(val code: Int) {
    OK(200),
    INTERNAL_SERVER_ERROR(500),
    BAD_REQUEST_ERROR(400),
    UNAUTHORIZED_ERROR(401),
    NOT_FOUND_ERROR(404)
}

class RemoteEnterRepository(private val client: EnterClientProvider): EnterRepository {
    override fun enter(model: Enter): Result<User> {
        val body = mapOf(
            Pair(Constant.EMAIL_KEY, model.email),
            Pair(Constant.PASSWORD_KEY, model.password),
        )
        val request = EnterRequest(Constant.POST_METHOD, body)
        val response = client.enter(request)

        return when (response.statusCode) {
            StatusCode.BAD_REQUEST_ERROR.code -> makeFailure(HTTPError.BadRequestError())
            StatusCode.UNAUTHORIZED_ERROR.code -> makeFailure(HTTPError.UnauthorizedError())
            StatusCode.INTERNAL_SERVER_ERROR.code -> makeFailure(HTTPError.InternalServerError())
            StatusCode.OK.code -> {
                response.data?.let {
                    val user = User(it. name, it.email)
                    return Result.success(user)
                }

                return makeFailure(HTTPError.NotFoundError())
            }
            StatusCode.NOT_FOUND_ERROR.code -> makeFailure(HTTPError.NotFoundError())

            else -> makeFailure(HTTPError.UnexpectedError())
        }
    }

    private fun makeFailure(error: Error): Result<User> = Result.failure(error)

    private object Constant {
        const val POST_METHOD = "POST"

        const val EMAIL_KEY = "email"
        const val PASSWORD_KEY = "password"
    }
}