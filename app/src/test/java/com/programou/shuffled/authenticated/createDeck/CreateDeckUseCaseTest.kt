package com.programou.shuffled.authenticated.createDeck

import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class CreateDeckUseCaseTest {
    @Test
    fun `test on 'createDeck' no completes without repository deleivers an response`() {
        val deck = CreateDeckModel("any title", "any description", "any image uri", false, listOf())
        val sut = CreateDeckUseCase(mock())

        val createDeckReceive = mutableListOf<Boolean>()
        sut.createDeck(deck) { createDeckReceive.add(it) }

        assertEquals(createDeckReceive, mutableListOf<Boolean>())
    }

    @Test
    fun `test on 'createDeck' calls repository 'saveDeck' method correct`() {
        val deck = CreateDeckModel("any title", "any description", "any image uri", false, listOf())
        val repositorySaveDeckArgument = argumentCaptor<(Boolean) -> Unit>()
        val repository: CreateDeckRepository = mock() {
            on { saveDeck(eq(deck), repositorySaveDeckArgument.capture()) } doAnswer {
                repositorySaveDeckArgument.firstValue.invoke(false)
            }
        }
        val sut = CreateDeckUseCase(repository)

        val createDeckReceive = mutableListOf<Boolean>()
        sut.createDeck(deck) { createDeckReceive.add(it) }

        verify(repository, times(1)).saveDeck(deck, repositorySaveDeckArgument.firstValue)
    }

    @Test
    fun `test on 'createDeck' when repository deleivers FALSE repass`() {
        val deck = CreateDeckModel("any title", "any description", "any image uri", false, listOf())
        val repositorySaveDeckArgument = argumentCaptor<(Boolean) -> Unit>()
        val repositorySaveDeckResponse = false
        val repository: CreateDeckRepository = mock() {
            on { saveDeck(eq(deck), repositorySaveDeckArgument.capture()) } doAnswer {
                repositorySaveDeckArgument.firstValue.invoke(repositorySaveDeckResponse)
            }
        }
        val sut = CreateDeckUseCase(repository)

        val createDeckReceive = mutableListOf<Boolean>()
        sut.createDeck(deck) { createDeckReceive.add(it) }

        assertEquals(createDeckReceive, mutableListOf(repositorySaveDeckResponse))
    }

    @Test
    fun `test on 'createDeck' when repository deleivers TRUE repass`() {
        val deck = CreateDeckModel("any title", "any description", "any image uri", false, listOf())
        val repositorySaveDeckArgument = argumentCaptor<(Boolean) -> Unit>()
        val repositorySaveDeckResponse = true
        val repository: CreateDeckRepository = mock() {
            on { saveDeck(eq(deck), repositorySaveDeckArgument.capture()) } doAnswer {
                repositorySaveDeckArgument.firstValue.invoke(repositorySaveDeckResponse)
            }
        }
        val sut = CreateDeckUseCase(repository)

        val createDeckReceive = mutableListOf<Boolean>()
        sut.createDeck(deck) { createDeckReceive.add(it) }

        assertEquals(createDeckReceive, mutableListOf(repositorySaveDeckResponse))
    }

    @Test
    fun `test on 'createDeck' when repository throws complete with FALSE`() {
        val deck = CreateDeckModel("any title", "any description", "any image uri", false, listOf())
        val repository: CreateDeckRepository = mock() {
            on { saveDeck(eq(deck), any()) } doThrow(Error("any exception error"))
        }
        val sut = CreateDeckUseCase(repository)

        val createDeckReceive = mutableListOf<Boolean>()
        sut.createDeck(deck) { createDeckReceive.add(it) }

        assertEquals(createDeckReceive, mutableListOf(false))
    }
}