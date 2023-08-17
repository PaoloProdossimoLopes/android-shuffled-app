package com.programou.shuffled.authenticated.deck.updateDeck.data

import com.programou.shuffled.authenticated.deck.updateDeck.domain.DeleteFlashcardsRepository
import com.programou.shuffled.authenticated.deck.updateDeck.domain.DeleteFlashcardsRequest
import com.programou.shuffled.database.ShuffledDatabase

class LocalDeleteFlashcardsRepository(private val database: ShuffledDatabase): DeleteFlashcardsRepository {
    override suspend fun delete(request: DeleteFlashcardsRequest) {
        database.cardDao().deleteCardWith(request.ids)
    }
}


