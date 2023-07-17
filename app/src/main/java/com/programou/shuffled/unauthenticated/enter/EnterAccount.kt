package com.programou.shuffled.unauthenticated.enter

interface EnterAccount {
    fun enter(model: com.programou.shuffled.unauthenticated.enter.Enter, callback: (Result<com.programou.shuffled.unauthenticated.enter.User>) -> Unit)
}