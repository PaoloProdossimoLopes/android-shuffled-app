package com.programou.shuffled.authenticated.deck.updateDeck.presentation

data class UpdateDeckViewData(
    val id: Long,
    val title: String,
    val description: String,
    val isFavorite: Boolean,
    val imageUri: String,
    val flashcardIds: List<Card>,
) {
    data class Card(
        val id: Long, val question: String,
        val answer: String, val studiesLeft: Int
    )
}