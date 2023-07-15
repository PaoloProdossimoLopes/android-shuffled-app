package com.programou.shuffled.unauthenticated.register

import com.programou.shuffled.unauthenticated.enter.User

interface RegisterAccount {
    fun register(model: Register, callback: (Result<User>) -> Unit)
}