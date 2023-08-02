package com.programou.shuffled.authenticated.flashcard

import android.content.Context
import com.programou.shuffled.database.ShuffledDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocalFlashcardRepository(private val context: Context): FlashcardClient {
    private val db: ShuffledDatabase by lazy {
        ShuffledDatabase.getDatabase(context)
    }

    override fun updateStudiesLeftsFor(cards: List<CardStudieUpdate>) {
        CoroutineScope(Dispatchers.IO).launch {
            for (card in cards) {
                db.cardDao().updateStudiesLeft(card.cardId, card.studiesLeft)
            }
        }
    }
}
