package com.programou.shuffled.authenticated.deck.updateDeck.main

import com.programou.shuffled.authenticated.deck.findDeck.data.LocalFindDeckRepository
import com.programou.shuffled.authenticated.deck.findDeck.infrastructure.FindDeckStoreAdapter
import com.programou.shuffled.authenticated.deck.updateDeck.data.LocalDeleteFlashcardsRepository
import com.programou.shuffled.authenticated.deck.updateDeck.data.LocalUpdateDeckRepository
import com.programou.shuffled.authenticated.deck.updateDeck.data.LocalUpdateFlashcardsRepository
import com.programou.shuffled.authenticated.deck.updateDeck.domain.UpdateDeck
import com.programou.shuffled.authenticated.deck.updateDeck.presentation.UpdateDeckPresenter
import com.programou.shuffled.authenticated.deck.updateDeck.presentation.UpdateDeckPresenting
import com.programou.shuffled.database.ShuffledDatabase

object UpdateDeckComposer {
    fun compose(database: ShuffledDatabase): UpdateDeckPresenting {
        val findDeckStore = FindDeckStoreAdapter(database)
        val findDeckRepository = LocalFindDeckRepository(findDeckStore)
        val updateDeckRepository = LocalUpdateDeckRepository(database)
        val deleteFlashcardRepository = LocalDeleteFlashcardsRepository(database)
        val updateFlashcardsRepository = LocalUpdateFlashcardsRepository(database)
        val updateDeckUseCase = UpdateDeck(
            findDeckRepository,
            updateDeckRepository,
            deleteFlashcardRepository,
            updateFlashcardsRepository
        )
        return UpdateDeckPresenter(updateDeckUseCase)
    }
}