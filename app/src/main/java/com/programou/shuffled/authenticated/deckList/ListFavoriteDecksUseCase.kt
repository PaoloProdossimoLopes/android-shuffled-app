package com.programou.shuffled.authenticated.deckList


interface ListFavoriteDecksRepository {
    fun listFavoritedDecks(onComplete: Bind<List<Deck>>)
}

class ListFavoriteDecksUseCase(private val repository: ListFavoriteDecksRepository): FavoriteListDecks {
    override fun listFavotes(result: Bind<DeckListViewData>) {
        try {
            repository.listFavoritedDecks { result(onListAllDecksSucessfully(it)) }
        } catch (_: Throwable) {
            val errorViewDataModel = DeckListViewData.Error(1, "Ops, algo inesperado ocorreu", "estamos tendo alguns problemas para se conectar com nossos serviços, tente novamente em alguns minutos")
            val errorViewData = DeckListViewData.error(errorViewDataModel)
            result(errorViewData)
        }
    }

    private fun onListAllDecksSucessfully(decks: List<Deck>): DeckListViewData {
        if (decks.isEmpty()) {
            val emptyViewDataModel = DeckListViewData.Empty(
                1, "Ops, nenhum baralho foi encontrado", "Parece que voce ainda nao possui baralhos registrados, crie um e começe seus estudos"
            )
            val emptyViewData = DeckListViewData.empty(emptyViewDataModel)
            return emptyViewData
        }

        val decksViewDataModel = decks.map { deck ->
            DeckListViewData.Deck(deck.id, deck.name, "${deck.numberOfCards}", deck.thumbnailUrl)
        }
        val decksViewData = DeckListViewData.decks(decksViewDataModel)
        return decksViewData
    }
}