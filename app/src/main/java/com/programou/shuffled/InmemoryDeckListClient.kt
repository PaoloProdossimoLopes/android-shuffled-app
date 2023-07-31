package com.programou.shuffled

import com.programou.shuffled.authenticated.createDeck.CreateDeckClient
import com.programou.shuffled.authenticated.createDeck.CreateDeckClientCompletionBlock
import com.programou.shuffled.authenticated.createDeck.CreateDeckModel
import com.programou.shuffled.authenticated.createDeck.CreateDeckResponse
import com.programou.shuffled.authenticated.deck.DeckClienting
import com.programou.shuffled.authenticated.deck.DeckResponse
import com.programou.shuffled.authenticated.deck.DeckUpdateClienting
import com.programou.shuffled.authenticated.deckList.Card
import com.programou.shuffled.authenticated.deckList.Deck
import com.programou.shuffled.authenticated.deckList.DeckListResponse
import com.programou.shuffled.authenticated.deckList.GetAllDecksClient
import com.programou.shuffled.authenticated.deckList.GetFavoritedDecksClient

class InmemoryDeckListClient private constructor(): GetAllDecksClient, GetFavoritedDecksClient,
    CreateDeckClient, DeckClienting, DeckUpdateClienting {

    companion object {
        val shared = InmemoryDeckListClient()
    }

    private val inmemoryDecks = mutableListOf(
        DeckListResponse.Deck(1, "Ingles", "Any description", "https://s4.static.brasilescola.uol.com.br/be/2022/05/bandeira-dos-estados-unidos.jpg", false, mutableListOf(
            DeckListResponse.Card(0,"How old are you?", "Quantos anos voce tem?", 0),
            DeckListResponse.Card(1,"What's up?", "E ai?", 0),
            DeckListResponse.Card(2,"Hello", "Olá", 1),
        )),
        DeckListResponse.Deck(2, "Frances", "Any description", "https://www.eurodicas.com.br/wp-content/uploads/2018/10/bandeira-da-franca-1200x900.jpg", false, mutableListOf(
            DeckListResponse.Card(0,"Bonjour", "Olá", 1),
            DeckListResponse.Card(1,"Comment le dire en français?", "Como dizer isso em francês?", 0),
            DeckListResponse.Card(2,"Que préfères-tu?", "Oque voce prefere?", 2),
            DeckListResponse.Card(3,"Pouvez-vous me l'épeler?", "Pode soletrar para mim?", 1),
            DeckListResponse.Card(4,"Quel âge as-tu?", "Quantos anos você tem", 1),
        )),
        DeckListResponse.Deck(3, "Jappones", "Any description", "https://ichef.bbci.co.uk/news/1024/branded_portuguese/135A8/production/_110227297_gettyimages-512612394.jpg", true, mutableListOf(
            DeckListResponse.Card(0,"よろしく", "Yoroshiku (Prazer em conhece-lo)", 3),
            DeckListResponse.Card(1,"いただきます", "Itadakimasu (Eu humildemente recebo)", 3),
            DeckListResponse.Card(2,"元気", "Genki (Como você está?)", 1),
            DeckListResponse.Card(3,"もったいない", "Mottainai (Desperdicio)", 3),
            DeckListResponse.Card(4,"すみません", "Sumimasen (Desculpe-me)", 2),
            DeckListResponse.Card(5,"がんばって", "Ganbatte (faça o seu melhor)", 1),
            DeckListResponse.Card(6,"しょうがない", "Shoganai (não tem jeito)", 1),
            DeckListResponse.Card(7,"気をつけてね", "Kiwotsukete ne (Tome cuidado)", 1),
            DeckListResponse.Card(8,"お疲れ", "Otsukare (Cansaço/ Fadiga)", 0),
        ))
    )
//    private val inmemoryDecks = mutableListOf<DeckListResponse.Deck>()

    override fun getAllDecks(callback: (DeckListResponse) -> Unit) {
        callback(DeckListResponse(inmemoryDecks))
//        throw Error()
    }
    override fun getFavorited(callback: (DeckListResponse) -> Unit) {
            callback(DeckListResponse(inmemoryDecks.filter { it.isFavorited }))
//            throw Error()
    }

    override fun postDeck(deck: CreateDeckModel, onComplete: CreateDeckClientCompletionBlock) {
        val id = inmemoryDecks.lastOrNull()?.id ?: 0
        inmemoryDecks.add(DeckListResponse.Deck(id + 1, deck.title, deck.description, deck.imageUri.toString(), deck.isFavorited, deck.cards.map { DeckListResponse.Card(it.id, it.question, it.answer, it.studiesLeft) }.toMutableList()))
        onComplete(CreateDeckResponse())

//                throw Error()
    }

    override suspend fun findBy(id: Int): DeckResponse? {
        val inmemoryDeck = inmemoryDecks.find { it.id == id }
        inmemoryDeck?.let {
            return DeckResponse(DeckResponse.Deck(it.id, it.title, it.description, it.thumbnailUrl, it.isFavorited, it.cards.map { DeckResponse.Card(it.id, it.question, it.answer) }.toMutableList()))
        }

        return null
    }

    override suspend fun updateDeck(deck: Deck): Boolean {
        val deckIndex = inmemoryDecks.indexOfFirst { it.id == deck.id }

        inmemoryDecks[deckIndex].title = deck.name
        inmemoryDecks[deckIndex].isFavorited = deck.isFavorite
        inmemoryDecks[deckIndex].description = deck.description
        inmemoryDecks[deckIndex].thumbnailUrl = deck.thumbnailUrl
        inmemoryDecks[deckIndex].cards = deck.cards.map { DeckListResponse.Card(it.id!!, it.question, it.awnser, it.studiesLeft) }.toMutableList()

        return true
    }

    override suspend fun createCard(deckId: Int, newCard: Card): Boolean {
        val deckIndex = inmemoryDecks.indexOfFirst { it.id == deckId }
        val newId = inmemoryDecks[deckIndex].cards.lastOrNull()?.id?.plus(1) ?: 0

        val all = inmemoryDecks[deckIndex].cards + DeckListResponse.Card(newId, newCard.question, newCard.awnser, newCard.studiesLeft)
        inmemoryDecks[deckIndex].cards = all.toMutableList()

        return true
    }

    override suspend fun updateFavorited(deckId: Int, isFavorited: Boolean): Boolean {
        val deckIndex = inmemoryDecks.indexOfFirst { it.id == deckId }
        inmemoryDecks[deckIndex].isFavorited = isFavorited
        return true
    }

    override suspend fun deleteDeck(id: Int): Boolean {
        val deckIndex = inmemoryDecks.indexOfFirst { it.id == id }
        inmemoryDecks.removeAt(deckIndex)
        return true
    }
}