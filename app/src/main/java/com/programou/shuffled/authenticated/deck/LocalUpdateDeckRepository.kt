package com.programou.shuffled.authenticated.deck

import android.content.Context
import com.programou.shuffled.authenticated.deckList.Card
import com.programou.shuffled.authenticated.deckList.Deck
import com.programou.shuffled.database.ShuffledDatabase
import com.programou.shuffled.database.entities.CardEntity
import com.programou.shuffled.database.entities.DeckEntity


class LocalUpdateDeckRepository(private val context: Context): DeckUpdateClienting {
    private val db: ShuffledDatabase by lazy {
        ShuffledDatabase.getDatabase(context)
    }

    override suspend fun updateDeck(deck: Deck): Boolean {
        val entity = DeckEntity(deck.name, deck.description, deck.isFavorite, deck.thumbnailUrl, deck.cards.map {
            val id = it.id!!.toLong()
            val card = CardEntity(it.question, it.awnser, it.studiesLeft)
            card.cardId = id
            db.cardDao().updateCard(card)

            id
        })
        entity.deckId = deck.id.toLong()
        db.deckDao().updateDeck(entity)
        return true
    }

    override suspend fun createCard(deckId: Int, newCard: Card): Boolean {
        val cardEntity = CardEntity(newCard.question, newCard.awnser, newCard.studiesLeft)
        val newCardId = db.cardDao().insertCard(cardEntity)
        val deck = db.deckDao().findDeckBy(deckId.toLong())

        db.deckDao().updateCardId(deckId.toLong(), deck.cardIds + newCardId)
        return true
    }

    override suspend fun updateFavorited(deckId: Int, isFavorited: Boolean): Boolean {
        db.deckDao().updateFavorite(deckId.toLong(), isFavorited)
        return true
    }

    override suspend fun deleteDeck(id: Int): Boolean {
        val deck = db.deckDao().findDeckBy(id.toLong())
        db.deckDao().deleteDeck(deck)
        return true
    }

    override suspend fun deleteCards(ids: List<Long>) {
        db.cardDao().deleteCardWith(ids)
    }
}