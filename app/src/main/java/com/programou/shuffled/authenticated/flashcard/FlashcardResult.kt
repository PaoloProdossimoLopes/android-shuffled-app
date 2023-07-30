package com.programou.shuffled.authenticated.flashcard

data class FlashcardResult(
    val deckTitle: String,
    val totalOfCards: Int,
    val numberOfEasy: Int,
    val numberOfMid: Int,
    val numberOfHard: Int
)