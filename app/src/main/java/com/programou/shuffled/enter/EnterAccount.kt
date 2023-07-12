package com.programou.shuffled.enter

interface EnterAccount {
    fun enter(model: Enter): Result<User>
}