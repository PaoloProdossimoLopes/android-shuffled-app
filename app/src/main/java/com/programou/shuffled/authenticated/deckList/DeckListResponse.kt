package com.programou.shuffled.authenticated.deckList

class DeckListResponse(val decks: List<Deck>) {
    class Deck(val id: Int, val title: String, val totalOfCards: Int, val thumbnailUrl: String, val isFavorited: Boolean)
}