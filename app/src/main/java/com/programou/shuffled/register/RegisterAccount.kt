package com.programou.shuffled.register

import com.programou.shuffled.enter.User

interface RegisterAccount {
    fun register(model: Register, callback: (Result<User>) -> Unit)
}