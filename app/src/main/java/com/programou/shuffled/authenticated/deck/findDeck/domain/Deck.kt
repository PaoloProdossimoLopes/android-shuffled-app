package com.programou.shuffled.authenticated.deck.findDeck.domain

data class Deck(
    val deckId: Long,
    val title: String,
    val description: String,
    val isFavorite: Boolean,
    val imageUri: String,
    val cardIds: List<Long>
)