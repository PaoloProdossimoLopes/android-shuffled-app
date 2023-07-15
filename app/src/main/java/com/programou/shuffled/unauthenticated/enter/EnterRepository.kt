package com.programou.shuffled.unauthenticated.enter

interface EnterRepository {
    fun enter(model: com.programou.shuffled.unauthenticated.enter.Enter, callback: (Result<User>) -> Unit)
}