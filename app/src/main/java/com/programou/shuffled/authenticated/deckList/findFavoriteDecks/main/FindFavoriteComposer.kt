package com.programou.shuffled.authenticated.deckList.findFavoriteDecks.main

import com.programou.shuffled.authenticated.deckList.findFavoriteDecks.data.LocalFindFavoritesRepository
import com.programou.shuffled.authenticated.deckList.findFavoriteDecks.domain.FindFavorites
import com.programou.shuffled.authenticated.deckList.findFavoriteDecks.presentation.FindFavoriteDecksPresenter
import com.programou.shuffled.authenticated.deckList.findFavoriteDecks.presentation.FindFavoriteDecksPresenting
import com.programou.shuffled.database.ShuffledDatabase

object FindFavoriteComposer {
    fun compose(database: ShuffledDatabase): FindFavoriteDecksPresenting {
        val repository = LocalFindFavoritesRepository(database)
        val useCase = FindFavorites(repository)
        return FindFavoriteDecksPresenter(useCase)
    }
}