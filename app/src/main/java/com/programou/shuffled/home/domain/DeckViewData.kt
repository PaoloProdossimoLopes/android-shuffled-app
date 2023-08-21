package com.programou.shuffled.home.domain

data class DeckViewData(val id: Long, val deckName: String, val numberOfFlashcards: String, val imageUrl: String) {
    constructor(deck: Deck): this(deck.id, deck.name, deck.flashcards.count().toString(), deck.imageUrl)
}