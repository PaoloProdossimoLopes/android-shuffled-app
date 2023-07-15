package com.programou.shuffled.unauthenticated.register

import com.programou.shuffled.unauthenticated.enter.UserResponse

interface RegisterClientProvider {
    fun register(request: RegisterRequest, callback: (UserResponse) -> Unit)
}