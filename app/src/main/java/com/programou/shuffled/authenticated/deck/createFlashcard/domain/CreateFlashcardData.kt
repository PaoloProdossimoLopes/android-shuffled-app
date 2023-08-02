package com.programou.shuffled.authenticated.deck.createFlashcard.domain

data class CreateFlashcardData(val deckId: Long, val flashcard: Flashcard) {
    data class Flashcard(val question: String, val answer: String)
}