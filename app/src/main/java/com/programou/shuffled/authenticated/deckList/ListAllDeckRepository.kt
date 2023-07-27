package com.programou.shuffled.authenticated.deckList

class RemoteListAllDeckRepository(private val client: GetAllDecksClient): ListAllDecksRepository {

    override fun listAllDecks(onComplete: Bind<List<Deck>>) {
        client.getAllDecks { response ->  onComplete(map(response)) }
    }

    private fun map(response: DeckListResponse) = response.decks.map { deck -> deck.toModel() }
}

private fun DeckListResponse.Deck.toModel() = Deck(id, title, cards.count(), thumbnailUrl, cards.map { Card(it.question, it.answer) })