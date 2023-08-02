package com.programou.shuffled.authenticated.deck.deleteDeck.data

interface DeleteDeckStore {
    suspend fun delete(task: DeleteDeckTask)
}