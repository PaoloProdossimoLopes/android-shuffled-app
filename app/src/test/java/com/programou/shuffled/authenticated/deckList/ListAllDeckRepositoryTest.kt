package com.programou.shuffled.authenticated.deckList

import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class ListAllDeckRepositoryTest {
    @Test
    fun `on listAllDecks call client's getAllDecks method once`() {
        val client = mock<GetAllDecksClient>()
        val sut = RemoteListAllDeckRepository(client)

        val decksReceived = mutableListOf<List<Deck>>()
        sut.listAllDecks { decksReceived.add(it) }

        verify(client, times(1)).getAllDecks(any())
        assertEquals(decksReceived.count(), 0)
    }

    @Test
    fun `on listAllDecks when client complete with empty list delievers an emoty list too`() {
        val clientCallback = argumentCaptor<((DeckListResponse) -> Unit)>()
        val decksResponse = DeckListResponse(listOf())
        val client = mock<GetAllDecksClient>() {
            on { getAllDecks(clientCallback.capture()) } doAnswer { clientCallback.firstValue.invoke(decksResponse) }
        }
        val sut = RemoteListAllDeckRepository(client)

        val decksReceived = mutableListOf<List<Deck>>()
        sut.listAllDecks { decksReceived.add(it) }

        assertEquals(decksReceived.toList(), listOf<List<Deck>>(listOf()))
    }

    @Test
    fun `on listAllDecks when client complete with non empty list delievers an non empty too`() {
        val clientCallback = argumentCaptor<((DeckListResponse) -> Unit)>()
        val decksResponse = DeckListResponse(listOf(
            DeckListResponse.Deck(0, "title 0", 0, "any url 0"),
            DeckListResponse.Deck(1, "title other 1", 23, "any url other 1"),
        ))
        val client = mock<GetAllDecksClient>() {
            on { getAllDecks(clientCallback.capture()) } doAnswer { clientCallback.firstValue.invoke(decksResponse) }
        }
        val sut = RemoteListAllDeckRepository(client)

        val decksReceived = mutableListOf<List<Deck>>()
        sut.listAllDecks { decksReceived.add(it) }

        assertEquals(decksReceived.toList(), listOf(listOf(
            Deck(0, "title 0", 0, "any url 0"),
            Deck(1, "title other 1", 23, "any url other 1")
        )))
    }
}