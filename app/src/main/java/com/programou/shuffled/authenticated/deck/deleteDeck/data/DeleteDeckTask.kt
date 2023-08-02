package com.programou.shuffled.authenticated.deck.deleteDeck.data

data class DeleteDeckTask(val deck: Deck) {
    data class Deck(
        val deckId: Long,
        val title: String,
        val description: String,
        val isFavorite: Boolean,
        val imageUri: String,
        val cardIds: List<Long>
    )
}