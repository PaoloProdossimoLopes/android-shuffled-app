package com.programou.shuffled.authenticated.deckList.findFavoriteDecks.data

import com.programou.shuffled.authenticated.deckList.findFavoriteDecks.domain.FavoriteDeck
import com.programou.shuffled.authenticated.deckList.findFavoriteDecks.domain.FavoriteDeckRepository
import com.programou.shuffled.database.ShuffledDatabase

class LocalFindFavoritesRepository(private val database: ShuffledDatabase): FavoriteDeckRepository {
    override suspend fun find(): List<FavoriteDeck> {
        return database.deckDao().getFavoriteDecks().map {
            FavoriteDeck(it.deckId, it.title, it.imageUri, it.cardIds.count())
        }
    }
}