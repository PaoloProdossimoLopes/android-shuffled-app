package com.programou.shuffled.authenticated.deck.updateFavorite.presenter

data class UpdateFavoriteEvent(val deckId: Long, val isFavorite: Boolean)