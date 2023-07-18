package com.programou.shuffled

import com.programou.shuffled.authenticated.deckList.Bind
import com.programou.shuffled.authenticated.deckList.Deck
import com.programou.shuffled.authenticated.deckList.DeckListResponse
import com.programou.shuffled.authenticated.deckList.GetAllDecksClient
import com.programou.shuffled.authenticated.deckList.ListAllDecksRepository

class InmemoryDeckListClient private constructor(): GetAllDecksClient {

    companion object {
        val shared = InmemoryDeckListClient()
    }

//    private val inmemoryDecks = mutableListOf(
//        DeckListResponse.Deck(0, "Ingles", 2, "https://s4.static.brasilescola.uol.com.br/be/2022/05/bandeira-dos-estados-unidos.jpg")
//    )
//    private val inmemoryDecks = mutableListOf<DeckListResponse.Deck>()

    override fun getAllDecks(callback: (DeckListResponse) -> Unit) {
//        callback(DeckListResponse(inmemoryDecks))
        throw Error()
    }
}