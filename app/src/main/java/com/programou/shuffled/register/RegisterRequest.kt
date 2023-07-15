package com.programou.shuffled.register

data class RegisterRequest(
    val method: String,
    val body: Map<String, String>
)