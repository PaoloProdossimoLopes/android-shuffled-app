package com.programou.shuffled.authenticated.deck.deleteDeck.data

import com.programou.shuffled.authenticated.deck.deleteDeck.domain.DeleteCardsRepository
import com.programou.shuffled.authenticated.deck.deleteDeck.domain.DeleteCardsRequest

class LocalDeleteCardsRepository(private val store: DeleteCardStore): DeleteCardsRepository {
    override suspend fun delete(request: DeleteCardsRequest) {
        val task = request.toTask()
        store.delete(task)
    }
}

private fun DeleteCardsRequest.toTask() = DeleteCardsTask(ids)

