package com.programou.shuffled.authenticated.deck.deleteDeck.data

import com.programou.shuffled.authenticated.deck.deleteDeck.domain.DeleteDeckRepository
import com.programou.shuffled.authenticated.deck.deleteDeck.domain.DeleteDeckRequest

class LocalDeleteDeckRepository(private val store: DeleteDeckStore): DeleteDeckRepository {
    override suspend fun delete(request: DeleteDeckRequest) {
        val task = request.toTask()
        store.delete(task)
    }
}

private fun DeleteDeckRequest.toTask() = DeleteDeckTask(DeleteDeckTask.Deck(
    deck.deckId,
    deck.title,
    deck.description,
    deck.isFavorite,
    deck.imageUri,
    deck.cardIds,
))