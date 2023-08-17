package com.programou.shuffled.authenticated.deck.findCards.data

import com.programou.shuffled.authenticated.deck.findCards.domain.Card
import com.programou.shuffled.authenticated.deck.findCards.domain.FindCardsRepository
import com.programou.shuffled.authenticated.deck.findCards.domain.FindCardsRequest

class LocalFindCardsRepository(private val store: FindCardsStore): FindCardsRepository {
    override suspend fun find(request: FindCardsRequest): List<Card> {
        val task = FindCardsTask(request.ids)
        return store.find(task)
    }
}