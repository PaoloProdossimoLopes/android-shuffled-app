package com.programou.shuffled.authenticated.deck

import java.io.Serializable

data class DeckModel(val deck: Deck): Serializable {

    data class Deck(
        val id: Int,
        val name: String,
        val numberOfCards: Int,
        val thumbnailUrl: String,
        val cards: List<Card>
    ): Serializable

    data class Card(
        val question: String,
        val awnser: String
    ): Serializable
}