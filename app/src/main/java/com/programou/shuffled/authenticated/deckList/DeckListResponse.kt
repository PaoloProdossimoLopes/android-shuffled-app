package com.programou.shuffled.authenticated.deckList

class DeckListResponse(val decks: List<Deck>) {
    class Deck(val id: Int, val title: String, val thumbnailUrl: String, val isFavorited: Boolean, val cards: List<Card>)
    class Card(val question: String, val answer: String)
}