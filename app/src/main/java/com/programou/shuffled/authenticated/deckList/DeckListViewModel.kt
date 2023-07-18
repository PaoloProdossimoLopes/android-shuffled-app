package com.programou.shuffled.authenticated.deckList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class DeckListViewModel(private val allLister: ListAllDecks) {

    private val decksViewDataMutableLiveData = MutableLiveData<DeckListViewData>()
    val decksViewData: LiveData<DeckListViewData> = decksViewDataMutableLiveData

    fun loadAllDecks() {
        allLister.listAll { result ->
            decksViewDataMutableLiveData.postValue(result)
        }
    }
}