package com.programou.shuffled.authenticated.deck.createFlashcard.presentation

data class CreateFlashcardViewData(
    val flashcard: Flashcard?,
    val hasError: Boolean
) {
    data class Flashcard(val question: String, val answer: String)
    data class Error(val reason: String)
}
