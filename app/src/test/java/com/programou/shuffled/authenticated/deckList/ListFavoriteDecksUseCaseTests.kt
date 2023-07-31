package com.programou.shuffled.authenticated.deckList

import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock

class ListFavoriteDecksUseCaseTests {
    @Test
    fun `on listAll without complete should not deleivers success or failure`() {
        val sut = ListFavoriteDecksUseCase(mock())

        val listAllCallback = mutableListOf<DeckListViewData>()
        sut.listFavotes { listAllCallback.add(it) }

        Assert.assertEquals(listAllCallback.count(), 0)
    }

    @Test
    fun `on listAll when complete empty list of decks delievers an empty state`() {
        val onRepositoryCallback = argumentCaptor<((List<Deck>) -> Unit)>()
        val listAllDecksRepository: ListFavoriteDecksRepository = mock() {
            on {
                listFavoritedDecks (onRepositoryCallback.capture())
            } doAnswer {
                onRepositoryCallback.firstValue.invoke(listOf())
            }
        }
        val sut = ListFavoriteDecksUseCase(listAllDecksRepository)

        val listAllCallback = mutableListOf<DeckListViewData>()
        sut.listFavotes { listAllCallback.add(it) }

        Assert.assertEquals(listAllCallback.count(), 1)
        Assert.assertEquals(
            listAllCallback.firstOrNull(), DeckListViewData.empty(
                DeckListViewData.Empty(
                    1,
                    "Ops, nenhum baralho foi encontrado",
                    "Parece que voce ainda nao possui baralhos registrados, crie um e começe seus estudos"
                )
            )
        )
    }

    @Test
    fun `on listAll when complete wiht no empty deleivers decks view data state`() {
        val onRepositoryCallback = argumentCaptor<((List<Deck>) -> Unit)>()
        val listAllDecksRepository: ListFavoriteDecksRepository = mock() {
            on {
                listFavoritedDecks(onRepositoryCallback.capture())
            } doAnswer {
                onRepositoryCallback.firstValue.invoke(listOf(
                    Deck(0, "any name 0", "any description 0", "https://any-url-0.com", true, listOf()),
                    Deck(1, "any name 1", "any description 1", "https://any-url-1.com", true, listOf()),
                    Deck(2, "any name 2", "any description 2", "https://any-url-2.com", true, listOf())
                ))
            }
        }
        val sut = ListFavoriteDecksUseCase(listAllDecksRepository)

        val listAllCallback = mutableListOf<DeckListViewData>()
        sut.listFavotes { listAllCallback.add(it) }

        Assert.assertEquals(listAllCallback.count(), 1)
        Assert.assertEquals(
            listAllCallback.firstOrNull(), DeckListViewData.decks(
                listOf(
                    DeckListViewData.Deck(0, "any name 0", "0", "https://any-url-0.com"),
                    DeckListViewData.Deck(1, "any name 1", "0", "https://any-url-1.com"),
                    DeckListViewData.Deck(2, "any name 2", "0", "https://any-url-2.com")
                )
            )
        )
    }

    @Test
    fun `on listAll when complete with failure (expcetion) deleivers an error state`() {
        val onRepositoryCallback = argumentCaptor<((List<Deck>) -> Unit)>()
        val listAllDecksRepository: ListFavoriteDecksRepository = mock() {
            on { listFavoritedDecks(onRepositoryCallback.capture()) } doAnswer { throw Error("Any error") }
        }
        val sut = ListFavoriteDecksUseCase(listAllDecksRepository)

        val listAllCallback = mutableListOf<DeckListViewData>()
        sut.listFavotes { listAllCallback.add(it) }

        Assert.assertEquals(listAllCallback.count(), 1)
        Assert.assertEquals(
            listAllCallback.firstOrNull(), DeckListViewData.error(
                DeckListViewData.Error(
                    1,
                    "Ops, algo inesperado ocorreu",
                    "estamos tendo alguns problemas para se conectar com nossos serviços, tente novamente em alguns minutos"
                )
            )
        )
    }
}