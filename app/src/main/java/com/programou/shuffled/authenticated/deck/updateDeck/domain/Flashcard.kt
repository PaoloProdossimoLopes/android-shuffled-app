package com.programou.shuffled.authenticated.deck.updateDeck.domain

data class Flashcard(
    val id: Long, val question: String,
    val answer: String, val studiesLeft: Int
)