package com.programou.shuffled.authenticated.deck.updateDeck.domain

interface DeleteFlashcardsRepository {
    suspend fun delete(request: DeleteFlashcardsRequest)
}