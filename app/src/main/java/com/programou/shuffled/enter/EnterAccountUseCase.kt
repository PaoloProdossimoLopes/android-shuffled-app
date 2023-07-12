package com.programou.shuffled.enter

class EnterAccountUseCase(private val repository: EnterRepository) {
    fun enter(model: Enter): Result<User> {
        val result = repository.enter(model)

        result.exceptionOrNull()?.let { _ ->
            val error = Error(CONSTANTS.INVALID_CREDENTIALS_MESSAGE)
            return Result.failure(error)
        }

        result.getOrNull()?.let { user ->
            return Result.success(user)
        }

        val error = Error(CONSTANTS.UNEXPECETED_ERROR_MESSAGE)
        return Result.failure(error)
    }

    private object CONSTANTS {
        const val INVALID_CREDENTIALS_MESSAGE = "Dados inválidos. Email ou senha estao incorretos."
        const val UNEXPECETED_ERROR_MESSAGE = "Ops, algo inesperado ocorreu, tente novamente mais tarde"
    }
}
