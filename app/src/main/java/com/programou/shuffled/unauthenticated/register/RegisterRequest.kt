package com.programou.shuffled.unauthenticated.register

data class RegisterRequest(
    val method: String,
    val body: Map<String, String>
)