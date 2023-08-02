package com.programou.shuffled.authenticated.deck.deleteDeck.infrastructure

import com.programou.shuffled.authenticated.deck.deleteDeck.data.FindDeckStore
import com.programou.shuffled.authenticated.deck.deleteDeck.data.FindDeckTask
import com.programou.shuffled.authenticated.deck.deleteDeck.domain.Deck
import com.programou.shuffled.database.ShuffledDatabase
import com.programou.shuffled.database.entities.DeckEntity

class FindDeckStoreAdapter(private val database: ShuffledDatabase): FindDeckStore {
    override suspend fun find(task: FindDeckTask): Deck {
        val dto = database.deckDao().findDeckBy(task.id)
        return dto.toModel()
    }
}

private fun DeckEntity.toModel() = Deck(deckId, title, description, isFavorite, imageUri, cardIds)
