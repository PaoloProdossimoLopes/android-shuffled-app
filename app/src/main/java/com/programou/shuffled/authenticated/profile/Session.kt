package com.programou.shuffled.authenticated.profile

interface Session {
    fun getCostumer(): Costumer
    fun logout()
}