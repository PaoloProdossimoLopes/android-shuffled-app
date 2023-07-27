package com.programou.shuffled.authenticated.createDeck

data class CreateDeckViewDataRequest(val title: String, val description: String, val imageUri: String?,val isFavorited: Boolean, val cards: List<Card>) {
    data class Card(val id: Int, val question: String, val awnser: String)
}