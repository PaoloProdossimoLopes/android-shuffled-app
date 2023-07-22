package com.programou.shuffled.authenticated.deckList

class RemoteListFavoritedDecksRepository(private val client: GetFavoritedDecksClient): ListFavoriteDecksRepository {

    override fun listFavoritedDecks(onComplete: Bind<List<Deck>>) = list(onComplete)

    private fun list(onComplete: Bind<List<Deck>>) {
        client.getFavorited { response ->  onComplete(map(response)) }
    }

    private fun map(response: DeckListResponse) = response.decks.map { deck -> deck.toModel() }
}

private fun DeckListResponse.Deck.toModel() = Deck(id, title, totalOfCards, thumbnailUrl)