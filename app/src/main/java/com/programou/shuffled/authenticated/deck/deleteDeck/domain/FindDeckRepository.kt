package com.programou.shuffled.authenticated.deck.deleteDeck.domain

interface FindDeckRepository {
    suspend fun find(request: FindDeckRequest): Deck
}