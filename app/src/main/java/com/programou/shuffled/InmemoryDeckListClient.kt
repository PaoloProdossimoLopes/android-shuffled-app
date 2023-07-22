package com.programou.shuffled

import com.programou.shuffled.authenticated.deckList.Bind
import com.programou.shuffled.authenticated.deckList.Deck
import com.programou.shuffled.authenticated.deckList.DeckListResponse
import com.programou.shuffled.authenticated.deckList.DeckListViewData
import com.programou.shuffled.authenticated.deckList.GetAllDecksClient
import com.programou.shuffled.authenticated.deckList.GetFavoritedDecksClient
import com.programou.shuffled.authenticated.deckList.ListAllDecksRepository

class InmemoryDeckListClient private constructor(): GetAllDecksClient, GetFavoritedDecksClient {

    companion object {
        val shared = InmemoryDeckListClient()
    }

    private val inmemoryDecks = mutableListOf(
        DeckListResponse.Deck(1, "Ingles", 32, "https://s4.static.brasilescola.uol.com.br/be/2022/05/bandeira-dos-estados-unidos.jpg", false),
        DeckListResponse.Deck(2, "Frances", 100,  "https://www.eurodicas.com.br/wp-content/uploads/2018/10/bandeira-da-franca-1200x900.jpg", false),
        DeckListResponse.Deck(3, "Jappones", 1, "https://ichef.bbci.co.uk/news/1024/branded_portuguese/135A8/production/_110227297_gettyimages-512612394.jpg", true)
    )
//    private val inmemoryDecks = mutableListOf<DeckListResponse.Deck>()

    override fun getAllDecks(callback: (DeckListResponse) -> Unit) {
//        callback(DeckListResponse(inmemoryDecks))
        throw Error()
    }

    override fun getFavorited(callback: (DeckListResponse) -> Unit) {
        callback(DeckListResponse(inmemoryDecks.filter { it.isFavorited }))
//        throw Error()
    }
}