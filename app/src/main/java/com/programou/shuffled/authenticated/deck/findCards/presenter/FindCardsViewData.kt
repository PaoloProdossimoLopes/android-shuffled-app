package com.programou.shuffled.authenticated.deck.findCards.presenter

data class FindCardsViewData(val cards: List<Card>) {
    data class Card(val id: Long, val question: String, val answer: String, val studiesLeft: Int)
}