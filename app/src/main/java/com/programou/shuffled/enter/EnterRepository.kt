package com.programou.shuffled.enter

interface EnterRepository {
    fun enter(model: Enter, callback: (Result<User>) -> Unit)
}