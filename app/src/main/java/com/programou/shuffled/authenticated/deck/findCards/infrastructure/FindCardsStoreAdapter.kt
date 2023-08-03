package com.programou.shuffled.authenticated.deck.findCards.infrastructure

import com.programou.shuffled.authenticated.deck.findCards.data.FindCardsStore
import com.programou.shuffled.authenticated.deck.findCards.data.FindCardsTask
import com.programou.shuffled.authenticated.deck.findCards.domain.Card
import com.programou.shuffled.database.ShuffledDatabase
import com.programou.shuffled.database.entities.CardEntity

class FindCardsStoreAdapter(private val database: ShuffledDatabase): FindCardsStore {
    override suspend fun find(task: FindCardsTask): List<Card> {
        val entities = database.cardDao().findCardsBy(task.ids)
        return entities.map { it.toModel() }
    }
}

private fun CardEntity.toModel() = Card(cardId, question, answer, studiesLeft)