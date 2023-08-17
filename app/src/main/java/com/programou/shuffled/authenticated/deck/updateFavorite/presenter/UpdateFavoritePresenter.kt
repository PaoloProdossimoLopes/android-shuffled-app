package com.programou.shuffled.authenticated.deck.updateFavorite.presenter

import com.programou.shuffled.authenticated.deck.updateFavorite.domain.FavoriteUpdater
import com.programou.shuffled.authenticated.deck.updateFavorite.domain.UpdateFavoriteData

class UpdateFavoritePresenter(private val updater: FavoriteUpdater): UpdateFavoritePresenting {
    override suspend fun updateFavorite(event: UpdateFavoriteEvent): UpdateFavoriteViewData {
        val data = UpdateFavoriteData(event.deckId, event.isFavorite)
        val deck = updater.updateFavorite(data)
        return UpdateFavoriteViewData(deck.isFavorite)
    }
}