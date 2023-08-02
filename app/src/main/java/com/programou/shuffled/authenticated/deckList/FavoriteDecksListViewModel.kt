package com.programou.shuffled.authenticated.deckList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

interface FavoriteListDecks {
    fun listFavotes(result: Bind<DeckListViewData>)
}

class FavoriteDecksListViewModel(private val favoriteLister: FavoriteListDecks): ViewModel() {
    class Factory(private val favoriteLister: FavoriteListDecks): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FavoriteDecksListViewModel(favoriteLister) as T
        }
    }

    private val favoriteDecksViewDataMutableLiveData = MutableLiveData<DeckListViewData>()
    val favoriteDecksViewData: LiveData<DeckListViewData> = favoriteDecksViewDataMutableLiveData

    fun loadAllDecks() {
        favoriteLister.listFavotes { result ->
            favoriteDecksViewDataMutableLiveData.postValue(result)
        }
    }
}