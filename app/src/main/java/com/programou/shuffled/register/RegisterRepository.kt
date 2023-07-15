package com.programou.shuffled.register

import com.programou.shuffled.enter.User

interface RegisterRepository {
    fun register(model: Register, callback: (Result<User>) -> Unit)
}