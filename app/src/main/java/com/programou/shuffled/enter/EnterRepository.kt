package com.programou.shuffled.enter

interface EnterRepository {
    fun enter(model: Enter): Result<User>
}