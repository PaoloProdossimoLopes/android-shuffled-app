package com.programou.shuffled.authenticated.deck.findCards.domain

class FindCards(private val repository: FindCardsRepository): CardsFinder {
    override suspend fun findCards(data: FindCardData): List<Card> {
        if (data.ids.isEmpty()) {
            return listOf()
        }

        return try {
            repository.find(FindCardsRequest(data.ids))
        } catch (_: Throwable) {
            listOf()
        }
    }
}