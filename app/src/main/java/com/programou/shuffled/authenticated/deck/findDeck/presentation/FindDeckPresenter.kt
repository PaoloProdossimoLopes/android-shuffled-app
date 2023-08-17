package com.programou.shuffled.authenticated.deck.findDeck.presentation

import com.programou.shuffled.authenticated.deck.findDeck.domain.Deck
import com.programou.shuffled.authenticated.deck.findDeck.domain.DeckFinder
import com.programou.shuffled.authenticated.deck.findDeck.domain.FindDeckData

class FindDeckPresenter(private val finder: DeckFinder): FindDeckPresenting {
    override suspend fun findDeck(event: FindDeckEvent): FindDeckViewData {
        val deck = finder.findDeck(event.toData())
        return deck.toViewData()
    }
}

private fun FindDeckEvent.toData() = FindDeckData(deckId)
private fun Deck.toViewData() = FindDeckViewData(FindDeckViewData.Deck(
    deckId, title, description, isFavorite, imageUri, cardIds
))