package com.programou.shuffled.authenticated.deck.updateDeck.presentation

data class UpdateDeckEvent(
    val deckId: Long,
    val newTitle: String,
    val newDescription: String,
    val newImageUri: String,
    val newFlashcardIds: List<Card>
) {
    data class Card(
        val id: Long, val question: String,
        val answer: String, val studiesLeft: Int
    )
}