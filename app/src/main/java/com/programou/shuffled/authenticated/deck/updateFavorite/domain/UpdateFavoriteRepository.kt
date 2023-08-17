package com.programou.shuffled.authenticated.deck.updateFavorite.domain

interface UpdateFavoriteRepository {
    suspend fun update(request: UpdateFavoriteRequest): UpdateFavoriteStored
}