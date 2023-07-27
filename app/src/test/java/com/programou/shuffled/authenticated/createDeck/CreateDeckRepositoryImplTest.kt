package com.programou.shuffled.authenticated.createDeck

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class CreateDeckRepositoryImplTest {
    @Test
    fun `test on 'saveDeck' no completes without until client responds`() {
        val deck = CreateDeckModel("any title", "any description", "")
        val sut = CreateDeckRepositoryImpl(mock())

        val saveDecksReceived = mutableListOf<Boolean>()
        sut.saveDeck(deck) { saveDecksReceived.add(it) }

        assertEquals(saveDecksReceived, mutableListOf<Boolean>())
    }

    @Test
    fun `test on 'saveDeck' calls client method correct`() {
        val deck = CreateDeckModel("any title", "any description", "")
        val deckResponse = CreateDeckResponse()
        val postClientCallbackArgument = argumentCaptor<CreateDeckClientCompletionBlock>()
        val client: CreateDeckClient = mock() {
            on { postDeck(eq(deck), postClientCallbackArgument.capture()) } doAnswer {
                postClientCallbackArgument.firstValue.invoke(deckResponse)
            }
        }
        val sut = CreateDeckRepositoryImpl(client)

        sut.saveDeck(deck, mock())

        verify(client, times(1)).postDeck(deck, postClientCallbackArgument.firstValue)
    }

    @Test
    fun `test on 'saveDeck' completes with false when client deleivers a NULL`() {
        val deck = CreateDeckModel("any title", "any description", "")
        val postClientCallbackArgument = argumentCaptor<CreateDeckClientCompletionBlock>()
        val client: CreateDeckClient = mock() {
            on { postDeck(eq(deck), postClientCallbackArgument.capture()) } doAnswer {
                postClientCallbackArgument.firstValue.invoke(null)
            }
        }
        val sut = CreateDeckRepositoryImpl(client)

        val saveDecksReceived = mutableListOf<Boolean>()
        sut.saveDeck(deck) { saveDecksReceived.add(it) }

        assertEquals(saveDecksReceived, mutableListOf(false))
    }

    @Test
    fun `test on 'saveDeck' deelivers TRUE when a client deleives a object`() {
        val deck = CreateDeckModel("any title", "any description", "")
        val postClientCallbackArgument = argumentCaptor<CreateDeckClientCompletionBlock>()
        val client: CreateDeckClient = mock() {
            on { postDeck(eq(deck), postClientCallbackArgument.capture()) } doAnswer {
                postClientCallbackArgument.firstValue.invoke(CreateDeckResponse())
            }
        }
        val sut = CreateDeckRepositoryImpl(client)

        val saveDecksReceived = mutableListOf<Boolean>()
        sut.saveDeck(deck) { saveDecksReceived.add(it) }

        assertEquals(saveDecksReceived, mutableListOf(true))
    }

    @Test
    fun `test on 'saveDeck' completes with failure if the client throws`() {
        val deck = CreateDeckModel("any title", "any description", "")
        val client: CreateDeckClient = mock() {
            on { postDeck(any(), any()) } doAnswer { throw ClientDeleiversAnException() }
        }
        val sut = CreateDeckRepositoryImpl(client)

        assertThrows(ClientDeleiversAnException::class.java) {
            sut.saveDeck(deck) {  }
        }
    }

    private class ClientDeleiversAnException: Throwable()
}