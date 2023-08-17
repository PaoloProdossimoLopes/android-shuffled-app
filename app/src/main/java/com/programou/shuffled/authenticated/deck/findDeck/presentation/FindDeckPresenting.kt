package com.programou.shuffled.authenticated.deck.findDeck.presentation

interface FindDeckPresenting {
    suspend fun findDeck(event: FindDeckEvent): FindDeckViewData
}
