package com.programou.shuffled.authenticated.deckList

import android.content.Context
import com.programou.shuffled.database.ShuffledDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocalListFavoriteDeckRespotory(private val context: Context): ListFavoriteDecksRepository {
    private val db: ShuffledDatabase by lazy {
        ShuffledDatabase.getDatabase(context)
    }

    override fun listFavoritedDecks(onComplete: Bind<List<Deck>>) {
        CoroutineScope(Dispatchers.IO).launch {
            val favoriteDecks = db.deckDao().getFavoriteDecks()

            onComplete(favoriteDecks.map {
                Deck(it.deckId.toInt(), it.title, it.description, it.imageUri, it.isFavorite, it.cardIds.map { cardId ->
                    val deckEntity = db.cardDao().findCardById(cardId)
                    Card(deckEntity.cardId.toInt(), deckEntity.question, deckEntity.answer, deckEntity.studiesLeft)
                })
            })
        }
    }
}