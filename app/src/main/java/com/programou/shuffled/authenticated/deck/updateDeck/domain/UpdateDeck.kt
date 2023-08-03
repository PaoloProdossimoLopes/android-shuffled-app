package com.programou.shuffled.authenticated.deck.updateDeck.domain

import com.programou.shuffled.authenticated.deck.findDeck.domain.FindDeckRepository
import com.programou.shuffled.authenticated.deck.findDeck.domain.FindDeckRequest

class UpdateDeck(
    private val findDeckRepository: FindDeckRepository,
    private val updateDeckRepository: UpdateDeckRepository,
    private val deleteFlashcardRepository: DeleteFlashcardsRepository,
    private val updateFlashcardsRepository: UpdateFlashcardsRepository
): DeckUpdater {

    override suspend fun updateDeck(data: UpdateDeckData): Deck {
        val findCurrentDeckRequest = FindDeckRequest(data.deckId)
        val currentDeck = findDeckRepository.find(findCurrentDeckRequest)

        updateDeck(data, currentDeck.isFavorite)

        val removedCardIds = mutableListOf<Long>()
        val updatedCards = mutableListOf<UpdateDeckData.Card>()
        for (card in data.newFlashcards) {
            if (currentDeck.cardIds.contains(card.id)) {
                updatedCards.add(card)
                continue
            }

            removedCardIds.add(card.id)
        }

        deleteRemovedFlashcards(removedCardIds)
        updateFlashcards(updatedCards)

        return Deck(
            currentDeck.deckId, data.newTitle,
            data.newDescription, currentDeck.isFavorite,
            data.newImageUri, data.newFlashcards.map { Deck.Card(it.id, it.question, it.answer, it.studiesLeft) }
        )
    }

    private suspend fun updateDeck(data: UpdateDeckData, deckIsFavorite: Boolean) {
        val updateDeckRequest = UpdateDeckRequest(
            data.deckId, data.newTitle,
            data.newDescription, deckIsFavorite,
            data.newImageUri, data.newFlashcards.map { it.id }
        )
        updateDeckRepository.update(updateDeckRequest)
    }

            private suspend fun deleteRemovedFlashcards(ids: List<Long>) {
        val deleteFlashcardsRequest = DeleteFlashcardsRequest(ids)
        deleteFlashcardRepository.delete(deleteFlashcardsRequest)
    }

    private suspend fun updateFlashcards(flashcards: List<UpdateDeckData.Card>) {
        val updateFlashcardsRequest = UpdateFlashcardsRequest(flashcards.map {
            UpdateFlashcardsRequest.Flashcard(it.id, it.question, it.answer, it.studiesLeft)
        })
        updateFlashcardsRepository.update(updateFlashcardsRequest)
    }
}