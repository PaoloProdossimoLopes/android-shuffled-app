package com.programou.shuffled.authenticated.deck.updateDeck.domain

data class UpdateDeckData(
    val deckId: Long,
    val newTitle: String,
    val newDescription: String,
    val newImageUri: String,
    val newFlashcards: List<Card>
) {
    data class Card(
        val id: Long, val question: String,
        val answer: String, val studiesLeft: Int
    )
}