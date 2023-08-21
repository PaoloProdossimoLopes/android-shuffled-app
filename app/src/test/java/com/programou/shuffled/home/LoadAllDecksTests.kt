package com.programou.shuffled.home

import com.programou.shuffled.home.domain.Deck
import com.programou.shuffled.home.domain.DeckRepository
import com.programou.shuffled.home.domain.DeckViewData
import com.programou.shuffled.home.domain.Flashcard
import com.programou.shuffled.home.domain.LoadAllDecks
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class LoadAllDecksTests {
    @Test
    fun `load should call repository once`() = runBlocking {
        val repository: DeckRepository = mock {
            onBlocking { findAll() } doReturn (listOf())
        }
        val sut = LoadAllDecks(repository)
        sut.load()
        verify(repository, times(1)).findAll()
        return@runBlocking
    }

    @Test
    fun `load should retrieve empty list of decks when repository returns a empty list`() = runBlocking {
        val repository: DeckRepository = mock {
            onBlocking { findAll() } doReturn (listOf())
        }
        val sut = LoadAllDecks(repository)
        val retrievedDecks = sut.load()
        assertTrue(retrievedDecks.isEmpty())
    }

    @Test
    fun `load should retrieve a list of decks equals repository deleivers`() = runBlocking {
        val decks = listOf(makeDeck())
        val repository: DeckRepository = mock {
            onBlocking { findAll() } doReturn (decks)
        }
        val sut = LoadAllDecks(repository)
        val findedDecks = sut.load()
        assertEquals(findedDecks, decks.map { DeckViewData(it) })
    }

    private fun makeDeck(
        id: Long = 0L,
        name: String = "deck title",
        description: String = "deck description",
        imageUrl: String = "https://deck-image-url.com",
        flashcards: List<Flashcard> = listOf(
            Flashcard(0L, "question 1", "answer 1", 0),
            Flashcard(1L, "question 2", "answer 3", 0),
        )
    ) = Deck(id, name, description, imageUrl, flashcards)
}