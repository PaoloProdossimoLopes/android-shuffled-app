package com.programou.shuffled.authenticated.deck.findDeck.domain

interface DeckFinder {
    suspend fun findDeck(data: FindDeckData): Deck
}