package com.programou.shuffled.authenticated.deckList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

interface FavoriteListDecks {
    fun listFavotes(result: Bind<DeckListViewData>)
}

class FavoriteDecksListViewModel(private val favoriteLister: FavoriteListDecks) {
    private val favoriteDecksViewDataMutableLiveData = MutableLiveData<DeckListViewData>()
    val favoriteDecksViewData: LiveData<DeckListViewData> = favoriteDecksViewDataMutableLiveData

    fun loadAllDecks() {
        favoriteLister.listFavotes { result ->
            favoriteDecksViewDataMutableLiveData.postValue(result)
        }
    }
}