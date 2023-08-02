package com.programou.shuffled.authenticated.deck.createFlashcard.presentation

import com.programou.shuffled.authenticated.deck.createFlashcard.domain.CreateFlashcardData
import com.programou.shuffled.authenticated.deck.createFlashcard.domain.Flashcard
import com.programou.shuffled.authenticated.deck.createFlashcard.domain.FlashcardCreator

class CreateFlashcardPresenter(private val creator: FlashcardCreator): CreateFlashcardPresenting {
    override suspend fun createFlashcard(event: CreateFlashcardEvent): CreateFlashcardViewData {
        val newFlashcard = creator.createFlashcard(event.toData())
        return newFlashcard.toViewData()
    }
}

private fun CreateFlashcardEvent.toData() = CreateFlashcardData(
    deckId,
    CreateFlashcardData.Flashcard(flashcard.question, flashcard.answer)
)

private fun Flashcard.toViewData() = CreateFlashcardViewData(
    CreateFlashcardViewData.Flashcard(question, answer),
    false
)