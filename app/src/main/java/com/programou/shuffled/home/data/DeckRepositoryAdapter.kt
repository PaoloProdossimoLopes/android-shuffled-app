package com.programou.shuffled.home.data

import com.programou.shuffled.database.ShuffledDatabase
import com.programou.shuffled.home.domain.Deck
import com.programou.shuffled.home.domain.DeckRepository
import com.programou.shuffled.home.domain.Flashcard

class DeckRepositoryAdapter(private val room: ShuffledDatabase): DeckRepository {
    override suspend fun findAll(): List<Deck> {
        val decksEntity = room.deckDao().getAllDecks()
        val decks = mutableListOf<Deck>()
        for (entity in decksEntity) {
            val flashcardEntities = room.cardDao().findCardsBy(entity.cardIds)
            val flashcards = flashcardEntities.map { flashcard ->
                Flashcard(flashcard.cardId, flashcard.question, flashcard.answer, flashcard.studiesLeft)
            }
            val deck = Deck(entity.deckId, entity.title, entity.description, entity.imageUri, flashcards)
            decks.add(deck)
        }
        return decks
    }
}