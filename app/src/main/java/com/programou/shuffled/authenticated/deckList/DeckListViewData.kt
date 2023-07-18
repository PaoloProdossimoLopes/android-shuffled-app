package com.programou.shuffled.authenticated.deckList

import java.io.Serializable

data class DeckListViewData private constructor(
    val decks: ViewDataModel<List<Deck>> = ViewDataModel(),
    val empty: ViewDataModel<Empty> = ViewDataModel(),
    val error: ViewDataModel<Error> = ViewDataModel()
) {

    data class Deck(val id: Int, val name: String, val numberOfCards: String, val thumbnailUrl: String)
    data class Empty(val imageId: Int, val title: String, val message: String)
    data class Error(val imageId: Int, val title: String, val reason: String)

    init {
        if (decks.exist() && empty.exist() && error.exist())
            throw Error("must not have at leat one non empty")
    }

    companion object {
        fun decks(itens: List<Deck>) = DeckListViewData(decks = ViewDataModel(itens))
        fun empty(value: Empty) = DeckListViewData(empty = ViewDataModel(value))
        fun error(value: Error) = DeckListViewData(error = ViewDataModel(value))
    }
}