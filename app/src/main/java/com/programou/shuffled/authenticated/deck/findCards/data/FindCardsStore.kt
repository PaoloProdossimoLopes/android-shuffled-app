package com.programou.shuffled.authenticated.deck.findCards.data

import com.programou.shuffled.authenticated.deck.findCards.domain.Card

interface FindCardsStore {
    suspend fun find(task: FindCardsTask): List<Card>
}