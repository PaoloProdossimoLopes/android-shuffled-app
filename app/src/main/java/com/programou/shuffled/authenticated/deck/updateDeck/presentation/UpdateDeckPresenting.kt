package com.programou.shuffled.authenticated.deck.updateDeck.presentation

interface UpdateDeckPresenting {
    suspend fun updateDeck(event: UpdateDeckEvent): UpdateDeckViewData
}