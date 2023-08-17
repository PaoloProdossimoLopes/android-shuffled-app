package com.programou.shuffled.authenticated.deckList.findAllDecks.presentation

import com.programou.shuffled.authenticated.deckList.findAllDecks.domain.AllDecksFinder

interface FindAllDecksPresenting {
    suspend fun findAll(): FindAllDecksViewData
}

data class FindAllDecksViewData(val decks: List<Deck>) {
    data class Deck(val id: Long, val title: String, val imageUri: String, val totalCards: String)
}

class FindAllDecksPresenter(private val favoriteFinder: AllDecksFinder): FindAllDecksPresenting {
    override suspend fun findAll(): FindAllDecksViewData {
        val favorites = favoriteFinder.findAllDecks()
        return FindAllDecksViewData(favorites.map {
            FindAllDecksViewData.Deck(
                it.id, it.title, it.imageUri, it.totalOfFlashcards.toString()
            )
        })
    }
}