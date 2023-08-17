package com.programou.shuffled.authenticated.deckList.findFavoriteDecks.domain

interface FavoriteFinder {
    suspend fun findFavorites(): List<FavoriteDeck>
}