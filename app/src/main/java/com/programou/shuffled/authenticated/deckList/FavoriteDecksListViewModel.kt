package com.programou.shuffled.authenticated.deckList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.programou.shuffled.authenticated.deckList.findFavoriteDecks.presentation.FindFavoriteDecksPresenting
import kotlinx.coroutines.launch

interface FavoriteListDecks {
    fun listFavotes(result: Bind<DeckListViewData>)
}

class FavoriteDecksListViewModel(private val favoriteLister: FindFavoriteDecksPresenting): ViewModel() {
    class Factory(private val favoriteLister: FindFavoriteDecksPresenting): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FavoriteDecksListViewModel(favoriteLister) as T
        }
    }

    private val favoriteDecksViewDataMutableLiveData = MutableLiveData<DeckListViewData>()
    val favoriteDecksViewData: LiveData<DeckListViewData> = favoriteDecksViewDataMutableLiveData

    fun loadAllDecks() = viewModelScope.launch {
        val decks = favoriteLister.findFavorites().decks

        if (decks.isEmpty()) {
            val viewData = DeckListViewData.Empty(1, "", "")
            favoriteDecksViewDataMutableLiveData.postValue(DeckListViewData.empty(viewData))
            return@launch
        }

        val favoriteViewDatas = DeckListViewData.decks(decks.map {
            DeckListViewData.Deck(it.id.toInt(), it.title, it.totalCards, it.imageUri)
        })
        favoriteDecksViewDataMutableLiveData.postValue(favoriteViewDatas)
    }
}