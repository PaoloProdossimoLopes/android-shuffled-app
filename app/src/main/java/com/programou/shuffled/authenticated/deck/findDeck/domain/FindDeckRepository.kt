package com.programou.shuffled.authenticated.deck.findDeck.domain
interface FindDeckRepository {
    suspend fun find(request: FindDeckRequest): Deck
}