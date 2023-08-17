package com.programou.shuffled.authenticated.deck.deleteDeck.data

interface DeleteCardStore {
    suspend fun delete(task: DeleteCardsTask)
}