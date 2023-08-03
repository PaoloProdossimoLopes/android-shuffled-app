package com.programou.shuffled.authenticated.deck.deleteDeck.main

import com.programou.shuffled.authenticated.deck.deleteDeck.data.LocalDeleteCardsRepository
import com.programou.shuffled.authenticated.deck.deleteDeck.data.LocalDeleteDeckRepository
import com.programou.shuffled.authenticated.deck.deleteDeck.domain.DeleteDeck
import com.programou.shuffled.authenticated.deck.deleteDeck.infrastructure.DeleteCardStoreAdapter
import com.programou.shuffled.authenticated.deck.deleteDeck.infrastructure.DeleteDeckStoreAdapter
import com.programou.shuffled.authenticated.deck.deleteDeck.presentation.DeleteDeckPresenter
import com.programou.shuffled.authenticated.deck.deleteDeck.presentation.DeleteDeckPresenting
import com.programou.shuffled.authenticated.deck.findDeck.data.LocalFindDeckRepository
import com.programou.shuffled.authenticated.deck.findDeck.infrastructure.FindDeckStoreAdapter
import com.programou.shuffled.database.ShuffledDatabase

object DeleteDeckComposer {
    fun compose(database: ShuffledDatabase): DeleteDeckPresenting {
        val findDeckStore = FindDeckStoreAdapter(database)
        val findDeckRepository = LocalFindDeckRepository(findDeckStore)

        val deleteDeckStore = DeleteDeckStoreAdapter(database)
        val deleteDeckRepository = LocalDeleteDeckRepository(deleteDeckStore)

        val deleteCardsStore = DeleteCardStoreAdapter(database)
        val deleteCardsRepository = LocalDeleteCardsRepository(deleteCardsStore)

        val deleteDeckUseCase = DeleteDeck(findDeckRepository, deleteDeckRepository, deleteCardsRepository)
        return DeleteDeckPresenter(deleteDeckUseCase)
    }
}