package com.programou.shuffled.authenticated.deckList

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import kotlin.random.Random

class DeckListViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `on loadAllDecks without completion should not emit for decks observer`() {
        val decksObserver: Observer<List<DeckListViewData.Deck>> = mock()
        val sut = DeckListViewModel(mock())
        sut.decks.observeForever(decksObserver)

        sut.loadAllDecks()

        verify(decksObserver, times(0)).onChanged(any())
    }

    @Test
    fun `on loadAllDecks should calls listAll method from 'ListAllDecks'`() {
        val allListerUseCase: ListAllDecks = mock()
        val sut = DeckListViewModel(allListerUseCase)

        sut.loadAllDecks()

        verify(allListerUseCase, times(1)).listAll(any())
    }

//    @Test
//    fun `on loadAllDecks when use case completes empty list`() {
//        val listViewData = DeckListViewData.empty(DeckListViewData.Empty(0, "any title", "any message"))
//        val decksObserver: Observer<List<DeckListViewData.Deck>> = mock()
//        val listAllCallback = argumentCaptor<((DeckListViewData) -> Unit)>()
//        val allListerUseCase: ListAllDecks = mock() {
//            on {
//                listAll(listAllCallback.capture())
//            } doAnswer {
//                listAllCallback.firstValue.invoke(listViewData)
//            }
//        }
//        val sut = DeckListViewModel(allListerUseCase)
//        sut.decks.observeForever(decksObserver)
//
//        sut.loadAllDecks()
//
//        verify(decksObserver, times(1)).onChanged(listOf())
//    }

    @Test
    fun `on loadAllDecks when use case completes non empty decks emit once`() {
        val decksObserver: Observer<List<DeckListViewData.Deck>> = mock()
        val listAllCallback = argumentCaptor<((DeckListViewData) -> Unit)>()
        val listViewData = DeckListViewData.decks(listOf(
            DeckListViewData.Deck(0, "name of deck 0", "number of cards in deck 0", "deck 0 thumbnail url"),
            DeckListViewData.Deck(1, "name of deck 1", "number of cards in deck 1", "deck 1 thumbnail url"),
            DeckListViewData.Deck(2, "name of deck 2", "number of cards in deck 2", "deck 2 thumbnail url")
        ))
        val allListerUseCase: ListAllDecks = mock() {
            on {
                listAll(listAllCallback.capture())
            } doAnswer {
                listAllCallback.firstValue.invoke(listViewData)
            }
        }
        val sut = DeckListViewModel(allListerUseCase)
        sut.decks.observeForever(decksObserver)

        sut.loadAllDecks()

        verify(decksObserver, times(1)).onChanged(listViewData.decks.value!!)
    }

    @Test
    fun `on loadAllDecks throws an error occurs exception`() {
        val deckListErrorObserver: Observer<DeckListViewData.Error> = mock()
        val deckListViewDataObserver: Observer<List<DeckListViewData.Deck>> = mock()
        val exception = Throwable("Any expcetion not handled by viewModel")
        val allListerUseCase: ListAllDecks = mock() {
            on { listAll(any()) } doAnswer { throw exception }
        }
        val sut = DeckListViewModel(allListerUseCase)
        sut.error.observeForever(deckListErrorObserver)
        sut.decks.observeForever(deckListViewDataObserver)

        verify(deckListErrorObserver, times(0)).onChanged(any())
        verify(deckListViewDataObserver, times(0)).onChanged(any())
        Assert.assertThrows(Throwable::class.java) {
            sut.loadAllDecks()
        }
    }

    @Test
    fun `on loadAllDecks when use case completes with use cases deleivers same viewData`() {
        val deckListErrorObserver: Observer<DeckListViewData.Error> = mock()
        val listAllCallback = argumentCaptor<((DeckListViewData) -> Unit)>()
        val errorListViewData = DeckListViewData.error(DeckListViewData.Error(0, "any title", "any message"))
        val allListerUseCase: ListAllDecks = mock() {
            on {
                listAll(listAllCallback.capture())
            } doAnswer {
                listAllCallback.firstValue.invoke(errorListViewData)
            }
        }
        val sut = DeckListViewModel(allListerUseCase)
        sut.error.observeForever(deckListErrorObserver)

        sut.loadAllDecks()

        verify(deckListErrorObserver, times(1)).onChanged(errorListViewData.error.value!!)
    }
}