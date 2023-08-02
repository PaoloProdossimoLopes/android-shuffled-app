package com.programou.shuffled.authenticated.deck.deleteDeck.data

import com.programou.shuffled.authenticated.deck.deleteDeck.domain.Deck

interface FindDeckStore {
    suspend fun find(task: FindDeckTask): Deck
}