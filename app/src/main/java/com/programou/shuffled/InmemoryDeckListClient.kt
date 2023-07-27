package com.programou.shuffled

import com.programou.shuffled.authenticated.createDeck.CreateDeckClient
import com.programou.shuffled.authenticated.createDeck.CreateDeckClientCompletionBlock
import com.programou.shuffled.authenticated.createDeck.CreateDeckModel
import com.programou.shuffled.authenticated.createDeck.CreateDeckResponse
import com.programou.shuffled.authenticated.deck.DeckClienting
import com.programou.shuffled.authenticated.deck.DeckResponse
import com.programou.shuffled.authenticated.deckList.DeckListResponse
import com.programou.shuffled.authenticated.deckList.GetAllDecksClient
import com.programou.shuffled.authenticated.deckList.GetFavoritedDecksClient

class InmemoryDeckListClient private constructor(): GetAllDecksClient, GetFavoritedDecksClient,
    CreateDeckClient, DeckClienting {

    companion object {
        val shared = InmemoryDeckListClient()
    }

    private val inmemoryDecks = mutableListOf(
        DeckListResponse.Deck(1, "Ingles", "Any description", "https://s4.static.brasilescola.uol.com.br/be/2022/05/bandeira-dos-estados-unidos.jpg", false, listOf(
            DeckListResponse.Card("How old are you?", "Quantos anos voce tem?"),
            DeckListResponse.Card("What's up?", "E ai?"),
            DeckListResponse.Card("Hello", "Olá"),
        )),
        DeckListResponse.Deck(2, "Frances", "Any description", "https://www.eurodicas.com.br/wp-content/uploads/2018/10/bandeira-da-franca-1200x900.jpg", false, listOf(
            DeckListResponse.Card("Bonjour", "Olá"),
            DeckListResponse.Card("Comment le dire en français?", "Como dizer isso em francês?"),
            DeckListResponse.Card("Que préfères-tu?", "Oque voce prefere?"),
            DeckListResponse.Card("Pouvez-vous me l'épeler?", "Pode soletrar para mim?"),
            DeckListResponse.Card("Quel âge as-tu?", "Quantos anos você tem"),
        )),
        DeckListResponse.Deck(3, "Jappones", "Any description", "https://ichef.bbci.co.uk/news/1024/branded_portuguese/135A8/production/_110227297_gettyimages-512612394.jpg", true, listOf(
            DeckListResponse.Card("よろしく", "Yoroshiku (Prazer em conhece-lo)"),
            DeckListResponse.Card("いただきます", "Itadakimasu (Eu humildemente recebo)"),
            DeckListResponse.Card("元気", "Genki (Como você está?)"),
            DeckListResponse.Card("もったいない", "Mottainai (Desperdicio)"),
            DeckListResponse.Card("すみません", "Sumimasen (Desculpe-me)"),
            DeckListResponse.Card("がんばって", "Ganbatte (faça o seu melhor)"),
            DeckListResponse.Card("しょうがない", "Shoganai (não tem jeito)"),
            DeckListResponse.Card("気をつけてね", "Kiwotsukete ne (Tome cuidado)"),
            DeckListResponse.Card("お疲れ", "Otsukare (Cansaço/ Fadiga)"),
        ))
    )
//    private val inmemoryDecks = mutableListOf<DeckListResponse.Deck>()

    override fun getAllDecks(callback: (DeckListResponse) -> Unit) {
        callback(DeckListResponse(inmemoryDecks))
//        throw Error()
    }

    override fun getFavorited(callback: (DeckListResponse) -> Unit) {
        callback(DeckListResponse(inmemoryDecks.filter { it.isFavorited }))
//        throw Error()
    }

    override fun postDeck(deck: CreateDeckModel, onComplete: CreateDeckClientCompletionBlock) {
        val id = inmemoryDecks.lastOrNull()?.id ?: 0
        inmemoryDecks.add(DeckListResponse.Deck(id, deck.title, deck.imageUri.toString(), deck.description, false, deck.cards.map { DeckListResponse.Card(it.question, it.answer) }))
        onComplete(CreateDeckResponse())

//                throw Error()
    }

    override suspend fun findBy(id: Int): DeckResponse? {
        val inmemoryDeck = inmemoryDecks.find { it.id == id }
        inmemoryDeck?.let {
            return DeckResponse(DeckResponse.Deck(it.id, it.title, it.description, it.thumbnailUrl, it.isFavorited, it.cards.map { DeckResponse.Card(it.question, it.answer) }))
        }

        return null
    }
}