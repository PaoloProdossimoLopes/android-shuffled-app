package com.programou.shuffled.authenticated.createDeck

import android.content.Context
import com.programou.shuffled.database.ShuffledDatabase
import com.programou.shuffled.database.entities.DeckEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocalCreateDeckRepository(private val context: Context): CreateDeckRepository {
    private val db: ShuffledDatabase by lazy {
        ShuffledDatabase.getDatabase(context)
    }

    override fun saveDeck(deck: CreateDeckModel, onComplete: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val cardEntities = deck.cards.map { it.id.toLong() }
            val deckEntity = DeckEntity(
                deck.title, deck.description, deck.isFavorited,
                deck.imageUri.toString(), cardEntities
            )
            db.deckDao().insert(deckEntity)

            onComplete(true)
        }
    }
}