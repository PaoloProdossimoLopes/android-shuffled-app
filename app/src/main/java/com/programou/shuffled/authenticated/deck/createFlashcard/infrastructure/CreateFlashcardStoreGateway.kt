package com.programou.shuffled.authenticated.deck.createFlashcard.infrastructure

import com.programou.shuffled.authenticated.deck.createFlashcard.data.CreateFlashcardStore
import com.programou.shuffled.authenticated.deck.createFlashcard.data.CreateFlashcardTask
import com.programou.shuffled.authenticated.deck.createFlashcard.data.UpdateFlashcardsInDeckTask
import com.programou.shuffled.database.ShuffledDatabase
import com.programou.shuffled.database.entities.CardEntity

class CreateFlashcardStoreGateway(private val database: ShuffledDatabase): CreateFlashcardStore {

    override suspend fun createFlashcard(task: CreateFlashcardTask): Long {
        val entity = CardEntity(task.question, task.answer, task.studiesLeft)
        return database.cardDao().insertCard(entity)
    }

    override suspend fun updateDeck(task: UpdateFlashcardsInDeckTask) {
        val deck = database.deckDao().findDeckBy(task.deckId)
        val allFlashcardIds = deck.cardIds + task.newFlashcardId
        deck.cardIds = allFlashcardIds
        database.deckDao().updateDeck(deck)
    }
}