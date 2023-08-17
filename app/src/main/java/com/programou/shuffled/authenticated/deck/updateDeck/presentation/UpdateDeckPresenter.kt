package com.programou.shuffled.authenticated.deck.updateDeck.presentation

import com.programou.shuffled.authenticated.deck.updateDeck.domain.DeckUpdater
import com.programou.shuffled.authenticated.deck.updateDeck.domain.UpdateDeckData

class UpdateDeckPresenter(private val updater: DeckUpdater): UpdateDeckPresenting {
    override suspend fun updateDeck(event: UpdateDeckEvent): UpdateDeckViewData {
        val cardDatas = event.newFlashcardIds.map {
            UpdateDeckData.Card(it.id, it.question, it.answer, it.studiesLeft)
        }
        val data = UpdateDeckData(event.deckId, event.newTitle, event.newDescription, event.newImageUri, cardDatas)
        val deck = updater.updateDeck(data)

        val flashcardViewDatas = deck.flashcardIds.map {
            UpdateDeckViewData.Card(it.id, it.question, it.answer, it.studiesLeft)
        }
        return UpdateDeckViewData(deck.id, deck.title, deck.description, deck.isFavorite, deck.imageUri, flashcardViewDatas)
    }
}