package com.programou.shuffled.authenticated.deckList

class DeckListResponse(val decks: List<Deck>) {
    class Deck(val id: Int, var title: String, var description: String, var thumbnailUrl: String, var isFavorited: Boolean, var cards: MutableList<Card>)
    class Card(val id: Int, var question: String, var answer: String)
}