package com.programou.shuffled.authenticated.deck.updateDeck.data

import com.programou.shuffled.authenticated.deck.updateDeck.domain.Flashcard
import com.programou.shuffled.authenticated.deck.updateDeck.domain.UpdateFlashcardsRepository
import com.programou.shuffled.authenticated.deck.updateDeck.domain.UpdateFlashcardsRequest
import com.programou.shuffled.database.ShuffledDatabase
import com.programou.shuffled.database.entities.CardEntity


class LocalUpdateFlashcardsRepository(private val database: ShuffledDatabase):
    UpdateFlashcardsRepository {
    override suspend fun update(request: UpdateFlashcardsRequest): List<Flashcard> {
        val entities = request.flahscards.map {
            val entity = CardEntity(it.question, it.answer, it.studiesLeft)
            entity.cardId = it.id
            entity
        }
        database.cardDao().updateCards(entities)

        return entities.map { it.toModel() }
    }
}

private fun CardEntity.toModel() = Flashcard(cardId, question, answer, studiesLeft)