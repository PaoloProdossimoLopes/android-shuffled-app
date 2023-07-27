package com.programou.shuffled.authenticated.createDeck

data class CreateDeckViewDataRequest(val title: String, val description: String, val imageUri: String?, val cards: List<Card>) {
    data class Card(val question: String, val awnser: String)
}