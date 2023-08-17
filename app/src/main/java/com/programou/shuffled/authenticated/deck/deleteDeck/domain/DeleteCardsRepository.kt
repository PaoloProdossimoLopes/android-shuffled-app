package com.programou.shuffled.authenticated.deck.deleteDeck.domain

interface DeleteCardsRepository {
    suspend fun delete(request: DeleteCardsRequest)
}
