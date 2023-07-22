package com.programou.shuffled.authenticated.deckList

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class FavoriteDecksListViewModelTests {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `on loadAllDecks without completion should not emit for decks observer`() {
        val decksObserver: Observer<DeckListViewData> = mock()
        val sut = FavoriteDecksListViewModel(mock())
        sut.favoriteDecksViewData.observeForever(decksObserver)

        sut.loadAllDecks()

        verify(decksObserver, times(0)).onChanged(any())
    }

    @Test
    fun `on loadAllDecks should calls listAll method from 'ListAllDecks'`() {
        val allListerUseCase: FavoriteListDecks = mock()
        val sut = FavoriteDecksListViewModel(allListerUseCase)

        sut.loadAllDecks()

        verify(allListerUseCase, times(1)).listFavotes(any())
    }

    @Test
    fun `on loadAllDecks when use case completes non empty decks emit once`() {
        val decksObserver: Observer<DeckListViewData> = mock()
        val listAllCallback = argumentCaptor<((DeckListViewData) -> Unit)>()
        val listViewData = DeckListViewData.decks(listOf(
            DeckListViewData.Deck(0, "name of deck 0", "number of cards in deck 0", "deck 0 thumbnail url"),
            DeckListViewData.Deck(1, "name of deck 1", "number of cards in deck 1", "deck 1 thumbnail url"),
            DeckListViewData.Deck(2, "name of deck 2", "number of cards in deck 2", "deck 2 thumbnail url")
        ))
        val allListerUseCase: FavoriteListDecks = mock() {
            on {
                listFavotes(listAllCallback.capture())
            } doAnswer {
                listAllCallback.firstValue.invoke(listViewData)
            }
        }
        val sut = FavoriteDecksListViewModel(allListerUseCase)
        sut.favoriteDecksViewData.observeForever(decksObserver)

        sut.loadAllDecks()

        verify(decksObserver, times(1)).onChanged(listViewData)
    }

    @Test
    fun `on loadAllDecks throws an error occurs exception`() {
        val deckListViewData: Observer<DeckListViewData> = mock()
        val exception = Throwable("Any expcetion not handled by viewModel")
        val allListerUseCase: FavoriteListDecks = mock() {
            on { listFavotes(any()) } doAnswer { throw exception }
        }
        val sut = FavoriteDecksListViewModel(allListerUseCase)
        sut.favoriteDecksViewData.observeForever(deckListViewData)

        verify(deckListViewData, times(0)).onChanged(any())
        Assert.assertThrows(Throwable::class.java) {
            sut.loadAllDecks()
        }
    }

    @Test
    fun `on loadAllDecks when use case completes with use cases deleivers same viewData`() {
        val deckListViewData: Observer<DeckListViewData> = mock()
        val listAllCallback = argumentCaptor<((DeckListViewData) -> Unit)>()
        val errorListViewData = DeckListViewData.error(DeckListViewData.Error(0, "any title", "any message"))
        val allListerUseCase: FavoriteListDecks = mock() {
            on {
                listFavotes(listAllCallback.capture())
            } doAnswer {
                listAllCallback.firstValue.invoke(errorListViewData)
            }
        }
        val sut = FavoriteDecksListViewModel(allListerUseCase)
        sut.favoriteDecksViewData.observeForever(deckListViewData)

        sut.loadAllDecks()

        verify(deckListViewData, times(1)).onChanged(errorListViewData)
    }


    @Test
    fun `on loadAllDecks when use case completes empty list`() {
        val listViewData = DeckListViewData.empty(DeckListViewData.Empty(0, "any title", "any message"))
        val decksObserver: Observer<DeckListViewData> = mock()
        val listAllCallback = argumentCaptor<((DeckListViewData) -> Unit)>()
        val allListerUseCase: FavoriteListDecks = mock() {
            on {
                listFavotes(listAllCallback.capture())
            } doAnswer {
                listAllCallback.firstValue.invoke(listViewData)
            }
        }
        val sut = FavoriteDecksListViewModel(allListerUseCase)
        sut.favoriteDecksViewData.observeForever(decksObserver)

        sut.loadAllDecks()

        verify(decksObserver, times(1)).onChanged(listViewData)
    }
}