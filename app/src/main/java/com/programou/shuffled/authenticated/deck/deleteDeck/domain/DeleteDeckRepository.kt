package com.programou.shuffled.authenticated.deck.deleteDeck.domain

interface DeleteDeckRepository {
    suspend fun delete(request: DeleteDeckRequest)
}