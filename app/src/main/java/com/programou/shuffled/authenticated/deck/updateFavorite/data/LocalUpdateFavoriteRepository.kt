package com.programou.shuffled.authenticated.deck.updateFavorite.data

import com.programou.shuffled.authenticated.deck.updateFavorite.domain.UpdateFavoriteRepository
import com.programou.shuffled.authenticated.deck.updateFavorite.domain.UpdateFavoriteRequest
import com.programou.shuffled.authenticated.deck.updateFavorite.domain.UpdateFavoriteStored
import com.programou.shuffled.database.ShuffledDatabase

class LocalUpdateFavoriteRepository(private val database: ShuffledDatabase): UpdateFavoriteRepository {
    override suspend fun update(request: UpdateFavoriteRequest): UpdateFavoriteStored {
        database.deckDao().updateFavorite(request.id, request.isFavorite)
        return UpdateFavoriteStored(request.isFavorite)
    }
}