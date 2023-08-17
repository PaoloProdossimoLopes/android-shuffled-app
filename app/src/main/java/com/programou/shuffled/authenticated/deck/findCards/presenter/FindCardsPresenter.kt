package com.programou.shuffled.authenticated.deck.findCards.presenter

import com.programou.shuffled.authenticated.deck.findCards.domain.Card
import com.programou.shuffled.authenticated.deck.findCards.domain.CardsFinder
import com.programou.shuffled.authenticated.deck.findCards.domain.FindCardData

class FindCardsPresenter(private val finder: CardsFinder): FindCardsPresenting {
    override suspend fun findCards(event: FindCardsEvent): FindCardsViewData {
        val cards = finder.findCards(event.toData())
        return FindCardsViewData(cards.map { it.toViewData() })
    }
}

private fun FindCardsEvent.toData() = FindCardData(cardIds)
private fun Card.toViewData() = FindCardsViewData.Card(id, question, answer, studiesLeft)