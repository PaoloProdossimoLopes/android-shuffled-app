package com.programou.shuffled.authenticated.createDeck

data class CreateDeckModel(val title: String, val description: String, val imageUri: String?, val isFavorited: Boolean,  val cards: List<Card>) {
    val numberOfDecks = 0

    data class Card(val id: Int, val question: String, val answer: String, val studiesLeft: Int)
}
