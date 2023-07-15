package com.programou.shuffled.unauthenticated.enter

class EnterAccountUseCase(private val repository: com.programou.shuffled.unauthenticated.enter.EnterRepository):
    com.programou.shuffled.unauthenticated.enter.EnterAccount {
    override fun enter(model: com.programou.shuffled.unauthenticated.enter.Enter, callback: (Result<com.programou.shuffled.unauthenticated.enter.User>) -> Unit) {
        repository.enter(model) { result ->

            result.exceptionOrNull()?.let { _ ->
                val error = Error(com.programou.shuffled.unauthenticated.enter.EnterAccountUseCase.CONSTANTS.INVALID_CREDENTIALS_MESSAGE)
                return@enter callback(Result.failure(error))
            }

            result.getOrNull()?.let { user ->
                return@enter callback(Result.success(user))
            }

            val error = Error(com.programou.shuffled.unauthenticated.enter.EnterAccountUseCase.CONSTANTS.UNEXPECETED_ERROR_MESSAGE)
            callback(Result.failure(error))
        }
    }

    private object CONSTANTS {
        const val INVALID_CREDENTIALS_MESSAGE = "Dados inválidos. Email ou senha estao incorretos."
        const val UNEXPECETED_ERROR_MESSAGE = "Ops, algo inesperado ocorreu, tente novamente mais tarde"
    }
}
