package com.programou.shuffled.authenticated.deck.updateDeck.data

import com.programou.shuffled.authenticated.deck.updateDeck.domain.UpdateDeckRepository
import com.programou.shuffled.authenticated.deck.updateDeck.domain.UpdateDeckRequest
import com.programou.shuffled.database.ShuffledDatabase
import com.programou.shuffled.database.entities.DeckEntity

class LocalUpdateDeckRepository(private val database: ShuffledDatabase): UpdateDeckRepository {
    override suspend fun update(request: UpdateDeckRequest): Long {
        val entity = DeckEntity(request.title, request.description, request.isFavorite, request.imageUri, request.flashcardIds)
        entity.deckId = request.id
        database.deckDao().updateDeck(entity)
        return entity.deckId
    }
}