package com.programou.shuffled.enter

class EnterAccountUseCase(private val repository: EnterRepository): EnterAccount {
    override fun enter(model: Enter, callback: (Result<User>) -> Unit) {
        repository.enter(model) { result ->

            result.exceptionOrNull()?.let { _ ->
                val error = Error(CONSTANTS.INVALID_CREDENTIALS_MESSAGE)
                return@enter callback(Result.failure(error))
            }

            result.getOrNull()?.let { user ->
                return@enter callback(Result.success(user))
            }

            val error = Error(CONSTANTS.UNEXPECETED_ERROR_MESSAGE)
            callback(Result.failure(error))
        }
    }

    private object CONSTANTS {
        const val INVALID_CREDENTIALS_MESSAGE = "Dados inválidos. Email ou senha estao incorretos."
        const val UNEXPECETED_ERROR_MESSAGE = "Ops, algo inesperado ocorreu, tente novamente mais tarde"
    }
}
