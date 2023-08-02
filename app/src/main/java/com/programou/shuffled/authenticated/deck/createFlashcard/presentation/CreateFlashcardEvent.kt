package com.programou.shuffled.authenticated.deck.createFlashcard.presentation

data class CreateFlashcardEvent(val deckId: Long, val flashcard: Flashcard) {
    data class Flashcard(val question: String, val answer: String)
}