package com.programou.shuffled.authenticated.deck.findCards.domain

interface CardsFinder {
    suspend fun findCards(data: FindCardData): List<Card>
}