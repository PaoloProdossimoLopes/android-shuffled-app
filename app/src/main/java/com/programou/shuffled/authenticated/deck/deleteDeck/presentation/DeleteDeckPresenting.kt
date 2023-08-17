package com.programou.shuffled.authenticated.deck.deleteDeck.presentation

interface DeleteDeckPresenting {
    suspend fun deleteDeck(event: DeleteDeckEvent)
}