package com.programou.shuffled.authenticated.deck.findCards.main

import com.programou.shuffled.authenticated.deck.findCards.data.LocalFindCardsRepository
import com.programou.shuffled.authenticated.deck.findCards.domain.FindCards
import com.programou.shuffled.authenticated.deck.findCards.infrastructure.FindCardsStoreAdapter
import com.programou.shuffled.authenticated.deck.findCards.presenter.FindCardsPresenter
import com.programou.shuffled.authenticated.deck.findCards.presenter.FindCardsPresenting
import com.programou.shuffled.database.ShuffledDatabase

object FindFlashcardsComposer {
    fun compose(database: ShuffledDatabase): FindCardsPresenting {
        val store = FindCardsStoreAdapter(database)
        val repository = LocalFindCardsRepository(store)
        val useCase = FindCards(repository)
        return FindCardsPresenter(useCase)
    }
}