package com.programou.shuffled.authenticated.deckList

interface GetAllDecksClient {
    fun getAllDecks(callback: (DeckListResponse) -> Unit)
}

class DeckListResponse(val decks: List<Deck>) {

    class Deck(val id: Int, val title: String, val totalOfCards: Int, val thumbnailUrl: String)
}

class RemoteListAllDeckRepository(private val client: GetAllDecksClient): ListAllDecksRepository {

    override fun listAllDecks(onComplete: Bind<List<Deck>>) {
        client.getAllDecks { response ->  onComplete(map(response)) }
    }

    private fun map(response: DeckListResponse) = response.decks.map { deck -> deck.toModel() }
}

private fun DeckListResponse.Deck.toModel() = Deck(id, title, totalOfCards, thumbnailUrl)