package com.programou.shuffled.home.domain

data class Deck(
    val id: Long,
    val name: String,
    val description: String,
    val imageUrl: String,
    val flashcards: List<Flashcard>
)