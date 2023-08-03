package com.programou.shuffled.authenticated.deck.updateFavorite.domain

class UpdateFavorite(private val repository: UpdateFavoriteRepository): FavoriteUpdater {
    override suspend fun updateFavorite(data: UpdateFavoriteData): Deck {
        val request = UpdateFavoriteRequest(data.deckId, data.isFavorite)
        val stored = repository.update(request)
        return Deck(stored.isFavorite)
    }
}