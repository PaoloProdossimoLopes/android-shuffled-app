package com.programou.shuffled.authenticated.deck.deleteDeck.domain

interface DeckDeletor {
    suspend fun deleteDeck(data: DeleteDeckData)
}