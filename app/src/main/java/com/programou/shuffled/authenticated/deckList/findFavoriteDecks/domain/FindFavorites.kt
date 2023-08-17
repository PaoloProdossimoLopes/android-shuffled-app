package com.programou.shuffled.authenticated.deckList.findFavoriteDecks.domain

class FindFavorites(private val repo: FavoriteDeckRepository): FavoriteFinder {
    override suspend fun findFavorites(): List<FavoriteDeck> {
        return repo.find()
    }
}