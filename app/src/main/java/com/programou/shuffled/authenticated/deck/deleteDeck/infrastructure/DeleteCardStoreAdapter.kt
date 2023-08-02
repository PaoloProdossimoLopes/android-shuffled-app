package com.programou.shuffled.authenticated.deck.deleteDeck.infrastructure

import com.programou.shuffled.authenticated.deck.deleteDeck.data.DeleteCardStore
import com.programou.shuffled.authenticated.deck.deleteDeck.data.DeleteCardsTask
import com.programou.shuffled.database.ShuffledDatabase

class DeleteCardStoreAdapter(private val database: ShuffledDatabase): DeleteCardStore {
    override suspend fun delete(task: DeleteCardsTask) {
        database.cardDao().deleteCardWith(task.ids)
    }
}