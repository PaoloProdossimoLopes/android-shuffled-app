package com.programou.shuffled.register

import com.programou.shuffled.enter.HTTPError
import com.programou.shuffled.enter.StatusCode
import com.programou.shuffled.enter.User


class RemoteRegisterRepository(private val client: RegisterClientProvider): RegisterRepository {

    private fun makeFailure(error: Error): Result<User> = Result.failure(error)

    private object Constant {
        const val POST_METHOD = "POST"

        const val EMAIL_KEY = "email"
        const val PASSWORD_KEY = "password"
    }

    override fun register(model: Register, callback: (Result<User>) -> Unit) {
        val body = mapOf(
            Pair(Constant.EMAIL_KEY, model.email),
            Pair(Constant.PASSWORD_KEY, model.password),
        )
        val request = RegisterRequest(Constant.POST_METHOD, body)

        client.register(request) { response ->
            val result = when (response.statusCode) {
                StatusCode.BAD_REQUEST_ERROR.code -> makeFailure(HTTPError.BadRequestError())
                StatusCode.UNAUTHORIZED_ERROR.code -> makeFailure(HTTPError.UnauthorizedError())
                StatusCode.INTERNAL_SERVER_ERROR.code -> makeFailure(HTTPError.InternalServerError())
                StatusCode.OK.code -> {
                    if (response.data != null) {
                        Result.success(User(response.data!!.name, response.data!!.email))
                    } else {
                        makeFailure(HTTPError.NotFoundError())
                    }
                }

                StatusCode.NOT_FOUND_ERROR.code -> makeFailure(HTTPError.NotFoundError())

                else -> makeFailure(HTTPError.UnexpectedError())
            }

            callback(result)
        }
    }
}