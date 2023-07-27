package com.programou.shuffled.authenticated.createDeck

data class CreateDeckModel(val title: String, val description: String, val imageUri: String?, val cards: List<Card>) {
    val numberOfDecks = 0

    data class Card(val question: String, val answer: String)
}
