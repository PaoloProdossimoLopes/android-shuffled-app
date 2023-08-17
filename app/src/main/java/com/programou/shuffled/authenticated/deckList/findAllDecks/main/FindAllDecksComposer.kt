package com.programou.shuffled.authenticated.deckList.findAllDecks.main

import com.programou.shuffled.authenticated.deckList.findAllDecks.data.LocalFindAllDecksRepository
import com.programou.shuffled.authenticated.deckList.findAllDecks.domain.FindAllDecks
import com.programou.shuffled.authenticated.deckList.findAllDecks.presentation.FindAllDecksPresenter
import com.programou.shuffled.authenticated.deckList.findAllDecks.presentation.FindAllDecksPresenting
import com.programou.shuffled.database.ShuffledDatabase

object FindAllDecksComposer {
    fun compose(database: ShuffledDatabase): FindAllDecksPresenting {
        val repository = LocalFindAllDecksRepository(database)
        val useCase = FindAllDecks(repository)
        return FindAllDecksPresenter(useCase)
    }
}