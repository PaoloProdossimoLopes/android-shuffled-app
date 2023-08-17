package com.programou.shuffled.authenticated.deck.findDeck.domain

class FindDeck(private val repository: FindDeckRepository): DeckFinder {
    override suspend fun findDeck(data: FindDeckData): Deck {
        try {
            return repository.find(data.toRequest())
        } catch (e: Throwable) {
            throw DeckNotFoundError()
        }
    }

    inner class DeckNotFoundError: Throwable()
}

private fun FindDeckData.toRequest() = FindDeckRequest(deckId)