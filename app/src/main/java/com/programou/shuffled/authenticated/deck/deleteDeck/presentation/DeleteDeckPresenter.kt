package com.programou.shuffled.authenticated.deck.deleteDeck.presentation

import com.programou.shuffled.authenticated.deck.deleteDeck.domain.Deck
import com.programou.shuffled.authenticated.deck.deleteDeck.domain.DeckDeletor
import com.programou.shuffled.authenticated.deck.deleteDeck.domain.DeleteDeckData

class DeleteDeckPresenter(private val deleter: DeckDeletor): DeleteDeckPresenting {
    override suspend fun deleteDeck(event: DeleteDeckEvent) {
        deleter.deleteDeck(event.map())
    }
}

private fun DeleteDeckEvent.map() = DeleteDeckData(id)