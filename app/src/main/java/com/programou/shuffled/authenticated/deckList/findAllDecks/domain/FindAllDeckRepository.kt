package com.programou.shuffled.authenticated.deckList.findAllDecks.domain

interface FindAllDeckRepository {
    suspend fun find(): List<Deck>
}
