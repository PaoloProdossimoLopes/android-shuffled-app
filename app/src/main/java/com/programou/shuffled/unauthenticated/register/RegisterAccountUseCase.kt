package com.programou.shuffled.unauthenticated.register

import com.programou.shuffled.unauthenticated.enter.User

class RegisterAccountUseCase(private val repository: RegisterRepository): RegisterAccount {
    override fun register(model: Register, callback: (Result<User>) -> Unit) {
        repository.register(model) { result ->

            result.exceptionOrNull()?.let { _ ->
                val error = Error(CONSTANTS.INVALID_CREDENTIALS_MESSAGE)
                return@register callback(Result.failure(error))
            }

            result.getOrNull()?.let { user ->
                return@register callback(Result.success(user))
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