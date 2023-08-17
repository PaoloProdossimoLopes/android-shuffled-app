package com.programou.shuffled.authenticated.deck.findDeck.data

import com.programou.shuffled.authenticated.deck.findDeck.domain.Deck

interface FindDeckStore {
    suspend fun find(task: FindDeckTask): Deck
}