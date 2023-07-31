package com.programou.shuffled.authenticated.result

import java.io.Serializable

data class ResultViewData(
    val deckId: Int,
    val deckTitle: String,
    val totalOfCards: Int,
    val numberOfEasy: Int,
    val numberOfMid: Int,
    val numberOfHard: Int
): Serializable
