package com.programou.shuffled.home.domain

data class Flashcard(
    val id: Long,
    val question: String,
    val answer: String,
    val studiesLeft: Int
)