package com.programou.shuffled.authenticated.deck.updateFavorite.domain

interface FavoriteUpdater {
    suspend fun updateFavorite(data: UpdateFavoriteData): Deck
}