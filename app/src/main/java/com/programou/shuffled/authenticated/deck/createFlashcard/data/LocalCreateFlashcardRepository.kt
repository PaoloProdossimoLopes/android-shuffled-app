package com.programou.shuffled.authenticated.deck.createFlashcard.data

import com.programou.shuffled.authenticated.deck.createFlashcard.domain.CreateFlashcardRepository
import com.programou.shuffled.authenticated.deck.createFlashcard.domain.CreateFlashcardRequest

class LocalCreateFlashcardRepository(private val store: CreateFlashcardStore):
    CreateFlashcardRepository {

    override suspend fun createFlashcard(request: CreateFlashcardRequest): Long {
        val newFlashcardId = createNewFlashcard(request.flashcard)

        updateDeckWithNewFlashcardId(request.deckId, newFlashcardId)

        return newFlashcardId
    }

    private suspend fun createNewFlashcard(flashcard: CreateFlashcardRequest.Flashcard): Long {
        val request = flashcard.toRequest()
        return store.createFlashcard(request)
    }

    private suspend fun updateDeckWithNewFlashcardId(deckId: Long, newFlashcardId: Long) {
        val updateTask = UpdateFlashcardsInDeckTask(newFlashcardId, deckId)
        store.updateDeck(updateTask)
    }
}

private fun CreateFlashcardRequest.Flashcard.toRequest() = CreateFlashcardTask(question, answer, studiesLeft)


