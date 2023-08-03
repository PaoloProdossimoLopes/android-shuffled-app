package com.programou.shuffled.authenticated.deck.updateFavorite.main

import com.programou.shuffled.authenticated.deck.updateFavorite.data.LocalUpdateFavoriteRepository
import com.programou.shuffled.authenticated.deck.updateFavorite.domain.UpdateFavorite
import com.programou.shuffled.authenticated.deck.updateFavorite.presenter.UpdateFavoritePresenter
import com.programou.shuffled.authenticated.deck.updateFavorite.presenter.UpdateFavoritePresenting
import com.programou.shuffled.database.ShuffledDatabase

object UpdateFavoriteComposer {
    fun compose(database: ShuffledDatabase): UpdateFavoritePresenting {
        val repository = LocalUpdateFavoriteRepository(database)
        val updater = UpdateFavorite(repository)
        return UpdateFavoritePresenter(updater)
    }
}