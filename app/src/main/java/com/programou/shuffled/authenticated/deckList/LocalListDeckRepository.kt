package com.programou.shuffled.authenticated.deckList

import android.content.Context
import com.programou.shuffled.database.ShuffledDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocalListDeckRepository(private val context: Context): ListAllDecksRepository {
    private val db: ShuffledDatabase by lazy {
        ShuffledDatabase.getDatabase(context)
    }

    override fun listAllDecks(onComplete: Bind<List<Deck>>) {
        CoroutineScope(Dispatchers.IO).launch {
            val decks = db.deckDao().getAllDecks()

            onComplete(decks.map {
                Deck(it.deckId.toInt(), it.title, it.description, it.imageUri, it.isFavorite, it.cardIds.map { cardId ->
                    val deckEntity = db.cardDao().findCardById(cardId)
                    Card(deckEntity.cardId.toInt(), deckEntity.question, deckEntity.answer, deckEntity.studiesLeft)
                })
            })
        }
    }
}