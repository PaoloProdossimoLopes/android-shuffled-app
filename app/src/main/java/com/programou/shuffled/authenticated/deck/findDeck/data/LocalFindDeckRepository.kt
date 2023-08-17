package com.programou.shuffled.authenticated.deck.findDeck.data

import com.programou.shuffled.authenticated.deck.findDeck.domain.Deck
import com.programou.shuffled.authenticated.deck.findDeck.domain.FindDeckRepository
import com.programou.shuffled.authenticated.deck.findDeck.domain.FindDeckRequest

class LocalFindDeckRepository(private val store: FindDeckStore): FindDeckRepository {
    override suspend fun find(request: FindDeckRequest): Deck {
        val task = request.toTask()
        return store.find(task)
    }
}
private fun FindDeckRequest.toTask() = FindDeckTask(id)