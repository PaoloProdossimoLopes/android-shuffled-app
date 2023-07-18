package com.programou.shuffled.authenticated.deckList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class DeckListViewModel(private val allLister: ListAllDecks) {

    private val errorMutableData = MutableLiveData<DeckListViewData.Error>()
    val error: LiveData<DeckListViewData.Error> = errorMutableData

    private val deckMutableData = MutableLiveData<List<DeckListViewData.Deck>>()
    val decks: LiveData<List<DeckListViewData.Deck>> = deckMutableData

    fun loadAllDecks() {
        allLister.listAll { result ->
            result.decks.on { decksViewData ->
                deckMutableData.postValue(decksViewData)
            }

            result.error.on { errorViewData ->
                errorMutableData.postValue(errorViewData)
            }
        }
    }
}