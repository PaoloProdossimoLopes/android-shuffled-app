package com.programou.shuffled.authenticated.deck.deleteDeck.data

import com.programou.shuffled.authenticated.deck.deleteDeck.domain.Deck
import com.programou.shuffled.authenticated.deck.deleteDeck.domain.FindDeckRepository
import com.programou.shuffled.authenticated.deck.deleteDeck.domain.FindDeckRequest

class LocalFindDeckRepository(private val store: FindDeckStore): FindDeckRepository {
    override suspend fun find(request: FindDeckRequest): Deck {
        val task = request.toTask()
        return store.find(task)
    }
}

private fun FindDeckRequest.toTask() = FindDeckTask(id)