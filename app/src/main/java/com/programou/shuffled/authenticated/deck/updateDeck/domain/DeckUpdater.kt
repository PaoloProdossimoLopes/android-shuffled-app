package com.programou.shuffled.authenticated.deck.updateDeck.domain

interface DeckUpdater {
    suspend fun updateDeck(data: UpdateDeckData): Deck
}