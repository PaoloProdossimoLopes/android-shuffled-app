package com.programou.shuffled.authenticated.deck.createFlashcard.main

import com.programou.shuffled.authenticated.deck.createFlashcard.data.LocalCreateFlashcardRepository
import com.programou.shuffled.authenticated.deck.createFlashcard.domain.CreateFlashcard
import com.programou.shuffled.authenticated.deck.createFlashcard.infrastructure.CreateFlashcardStoreGateway
import com.programou.shuffled.authenticated.deck.createFlashcard.presentation.CreateFlashcardPresenter
import com.programou.shuffled.authenticated.deck.createFlashcard.presentation.CreateFlashcardPresenting
import com.programou.shuffled.authenticated.deck.deleteDeck.presentation.DeleteDeckPresenting
import com.programou.shuffled.database.ShuffledDatabase

object CreateFlashcardComposer {
    fun compose(database: ShuffledDatabase): CreateFlashcardPresenting {
        val createFlashCardStore = CreateFlashcardStoreGateway(database)
        val createFlashcardRepository = LocalCreateFlashcardRepository(createFlashCardStore)
        val createFlashcardUseCase = CreateFlashcard(createFlashcardRepository)
        return CreateFlashcardPresenter(createFlashcardUseCase)
    }
}