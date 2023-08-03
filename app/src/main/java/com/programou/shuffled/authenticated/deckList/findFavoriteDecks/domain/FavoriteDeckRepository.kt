package com.programou.shuffled.authenticated.deckList.findFavoriteDecks.domain

interface FavoriteDeckRepository {
    suspend fun find(): List<FavoriteDeck>
}
