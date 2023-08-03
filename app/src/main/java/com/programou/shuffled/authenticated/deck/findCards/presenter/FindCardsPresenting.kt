package com.programou.shuffled.authenticated.deck.findCards.presenter

interface FindCardsPresenting {
    suspend fun findCards(event: FindCardsEvent): FindCardsViewData
}