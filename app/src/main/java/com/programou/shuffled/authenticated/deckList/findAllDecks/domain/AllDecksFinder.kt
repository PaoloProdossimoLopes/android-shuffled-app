package com.programou.shuffled.authenticated.deckList.findAllDecks.domain

interface AllDecksFinder {
    suspend fun findAllDecks(): List<Deck>
}