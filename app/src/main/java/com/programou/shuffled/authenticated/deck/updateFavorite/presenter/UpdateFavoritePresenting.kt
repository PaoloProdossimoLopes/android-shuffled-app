package com.programou.shuffled.authenticated.deck.updateFavorite.presenter

interface UpdateFavoritePresenting {
    suspend fun updateFavorite(event: UpdateFavoriteEvent): UpdateFavoriteViewData
}



