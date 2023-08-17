package com.programou.shuffled.authenticated.deck.findDeck.main

import com.programou.shuffled.authenticated.deck.findDeck.data.LocalFindDeckRepository
import com.programou.shuffled.authenticated.deck.findDeck.domain.FindDeck
import com.programou.shuffled.authenticated.deck.findDeck.infrastructure.FindDeckStoreAdapter
import com.programou.shuffled.authenticated.deck.findDeck.presentation.FindDeckPresenter
import com.programou.shuffled.authenticated.deck.findDeck.presentation.FindDeckPresenting
import com.programou.shuffled.database.ShuffledDatabase

object FindDeckComposer {
    fun compose(database: ShuffledDatabase): FindDeckPresenting {
        val findDeckStore = FindDeckStoreAdapter(database)
        val findDeckRepository = LocalFindDeckRepository(findDeckStore)
        val findDeckUseCase = FindDeck(findDeckRepository)
        return FindDeckPresenter(findDeckUseCase)
    }
}