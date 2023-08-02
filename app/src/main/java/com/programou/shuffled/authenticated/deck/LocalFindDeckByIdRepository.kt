package com.programou.shuffled.authenticated.deck

import android.content.Context
import com.programou.shuffled.database.ShuffledDatabase

class LocalFindDeckByIdRepository(private val context: Context): DeckClienting {
    private val db: ShuffledDatabase by lazy {
        ShuffledDatabase.getDatabase(context)
    }

    override suspend fun findBy(id: Int): DeckResponse? {
        val deck = db.deckDao().findDeckBy(id.toLong())

        val cardResponse = deck.cardIds.map {
            val card = db.cardDao().findCardById(it)
            DeckResponse.Card(card.cardId.toInt(), card.question, card.answer, card.studiesLeft)
        }.toMutableList()
        val deckResponse = DeckResponse.Deck(deck.deckId.toInt(), deck.title, deck.description, deck.imageUri, deck.isFavorite, cardResponse)
        return DeckResponse(deckResponse)
    }
}
