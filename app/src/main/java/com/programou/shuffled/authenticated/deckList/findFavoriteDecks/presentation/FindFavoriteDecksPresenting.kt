package com.programou.shuffled.authenticated.deckList.findFavoriteDecks.presentation

import com.programou.shuffled.authenticated.deckList.findFavoriteDecks.domain.FavoriteFinder

interface FindFavoriteDecksPresenting {
    suspend fun findFavorites(): FindFavoriteDecksViewData
}

data class FindFavoriteDecksViewData(val decks: List<Deck>) {
    data class Deck(val id: Long, val title: String, val imageUri: String, val totalCards: String)
}

class FindFavoriteDecksPresenter(private val favoriteFinder: FavoriteFinder): FindFavoriteDecksPresenting {
    override suspend fun findFavorites(): FindFavoriteDecksViewData {
        val favorites = favoriteFinder.findFavorites()
        return FindFavoriteDecksViewData(favorites.map {
            FindFavoriteDecksViewData.Deck(
                it.id, it.title, it.imageUri, it.totalOfFlashcards.toString()
            )
        })
    }
}