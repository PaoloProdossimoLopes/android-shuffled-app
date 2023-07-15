package com.programou.shuffled.enter

interface EnterAccount {
    fun enter(model: Enter, callback: (Result<User>) -> Unit)
}