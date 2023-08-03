package com.programou.shuffled.authenticated.deckList.findAllDecks.domain

class FindAllDecks(private val repo: FindAllDeckRepository): AllDecksFinder {
    override suspend fun findAllDecks(): List<Deck> {
        return repo.find()
    }
}