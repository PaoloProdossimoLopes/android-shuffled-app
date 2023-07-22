package com.programou.shuffled.authenticated.deckList

interface GetAllDecksClient {
    fun getAllDecks(callback: (DeckListResponse) -> Unit)
}