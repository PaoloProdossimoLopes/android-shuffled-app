package com.programou.shuffled.authenticated.deckList

interface GetFavoritedDecksClient {
    fun getFavorited(callback: (DeckListResponse) -> Unit)
}