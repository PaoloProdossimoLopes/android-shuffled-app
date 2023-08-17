package com.programou.shuffled.authenticated.deck.findCards.domain

interface FindCardsRepository {
    suspend fun find(request: FindCardsRequest): List<Card>
}