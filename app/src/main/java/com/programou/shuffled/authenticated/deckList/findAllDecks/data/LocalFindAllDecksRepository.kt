package com.programou.shuffled.authenticated.deckList.findAllDecks.data

import com.programou.shuffled.authenticated.deckList.findAllDecks.domain.Deck
import com.programou.shuffled.authenticated.deckList.findAllDecks.domain.FindAllDeckRepository
import com.programou.shuffled.database.ShuffledDatabase

class LocalFindAllDecksRepository(private val database: ShuffledDatabase): FindAllDeckRepository {
    override suspend fun find(): List<Deck> {
        return database.deckDao().getAllDecks().map {
            Deck(it.deckId, it.title, it.imageUri, it.cardIds.count())
        }
    }
}