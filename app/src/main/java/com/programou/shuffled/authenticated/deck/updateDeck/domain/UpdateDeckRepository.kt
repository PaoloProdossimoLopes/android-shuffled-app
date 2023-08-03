package com.programou.shuffled.authenticated.deck.updateDeck.domain

interface UpdateDeckRepository {
    suspend fun update(request: UpdateDeckRequest): Long
}