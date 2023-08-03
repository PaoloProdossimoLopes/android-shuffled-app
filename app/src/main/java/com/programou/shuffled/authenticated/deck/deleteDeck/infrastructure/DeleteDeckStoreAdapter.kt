package com.programou.shuffled.authenticated.deck.deleteDeck.infrastructure

import com.programou.shuffled.authenticated.deck.deleteDeck.data.DeleteDeckStore
import com.programou.shuffled.authenticated.deck.deleteDeck.data.DeleteDeckTask
import com.programou.shuffled.database.ShuffledDatabase
import com.programou.shuffled.database.entities.DeckEntity

class DeleteDeckStoreAdapter(private val database: ShuffledDatabase): DeleteDeckStore {
    override suspend fun delete(task: DeleteDeckTask) {
        val entity = task.toEntity()
        database.deckDao().deleteDeck(entity)
    }
}

fun DeleteDeckTask.toEntity(): DeckEntity {
    with (deck) {
        val entity = DeckEntity(title, description, isFavorite, imageUri, cardIds)
        entity.deckId = deckId
        return  entity
    }
}