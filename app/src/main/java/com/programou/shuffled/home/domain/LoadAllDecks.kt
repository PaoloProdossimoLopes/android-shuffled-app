package com.programou.shuffled.home.domain

class LoadAllDecks(private val repository: DeckRepository): AllDecksLoader {
    override suspend fun load(): List<DeckViewData> {
        val decks = repository.findAll()
        return decks.map { DeckViewData(it) }
    }
}