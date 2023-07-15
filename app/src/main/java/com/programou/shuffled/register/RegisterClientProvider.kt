package com.programou.shuffled.register

import com.programou.shuffled.enter.UserResponse

interface RegisterClientProvider {
    fun register(request: RegisterRequest, callback: (UserResponse) -> Unit)
}