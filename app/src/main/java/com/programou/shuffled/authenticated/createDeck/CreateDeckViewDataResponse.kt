package com.programou.shuffled.authenticated.createDeck

class CreateDeckViewDataResponse {
    data class Success(val message: String)
    data class Error(val reason: String)
}