package com.programou.shuffled.authenticated.deck.updateDeck.domain

data class UpdateDeckRequest(
    val id: Long,
    val title: String,
    val description: String,
    val isFavorite: Boolean,
    val imageUri: String,
    val flashcardIds: List<Long>,
)