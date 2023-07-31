package com.programou.shuffled.authenticated.deckList

import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class RemoteListFavoritedDecksRepositoryTests {
    @Test
    fun `on listAllDecks call client's getAllDecks method once`() {
        val client = mock<GetFavoritedDecksClient>()
        val sut = RemoteListFavoritedDecksRepository(client)

        val decksReceived = mutableListOf<List<Deck>>()
        sut.listFavoritedDecks { decksReceived.add(it) }

        verify(client, times(1)).getFavorited(any())
        Assert.assertEquals(decksReceived.count(), 0)
    }

    @Test
    fun `on listAllDecks when client complete with empty list delievers an emoty list too`() {
        val clientCallback = argumentCaptor<((DeckListResponse) -> Unit)>()
        val decksResponse = DeckListResponse(listOf())
        val client = mock<GetFavoritedDecksClient>() {
            on { getFavorited(clientCallback.capture()) } doAnswer { clientCallback.firstValue.invoke(decksResponse) }
        }
        val sut = RemoteListFavoritedDecksRepository(client)

        val decksReceived = mutableListOf<List<Deck>>()
        sut.listFavoritedDecks { decksReceived.add(it) }

        Assert.assertEquals(decksReceived.toList(), listOf<List<Deck>>(listOf()))
    }

    @Test
    fun `on listAllDecks when client complete with non empty list delievers an non empty too`() {
        val clientCallback = argumentCaptor<((DeckListResponse) -> Unit)>()
        val decksResponse = DeckListResponse(listOf(
            DeckListResponse.Deck(0, "title 0", "description 0", "any url 0", true, mutableListOf()),
            DeckListResponse.Deck(1, "title other 1", "description 1", "any url other 1", true, mutableListOf()),
        ))
        val client = mock<GetFavoritedDecksClient>() {
            on { getFavorited(clientCallback.capture()) } doAnswer { clientCallback.firstValue.invoke(decksResponse) }
        }
        val sut = RemoteListFavoritedDecksRepository(client)

        val decksReceived = mutableListOf<List<Deck>>()
        sut.listFavoritedDecks { decksReceived.add(it) }

        Assert.assertEquals(
            decksReceived.toList(), listOf(
                listOf(
                    Deck(0, "title 0", "description 0", "any url 0", true, listOf()),
                    Deck(1, "title other 1", "description 1", "any url other 1", true, listOf())
                )
            )
        )
    }
}