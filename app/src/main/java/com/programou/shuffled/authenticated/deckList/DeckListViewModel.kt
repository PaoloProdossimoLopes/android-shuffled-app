package com.programou.shuffled.authenticated.deckList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DeckListViewModel(private val allLister: ListAllDecks): ViewModel() {

    class Factory(private val allLister: ListAllDecks): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DeckListViewModel(allLister) as T
        }
    }


    private val decksViewDataMutableLiveData = MutableLiveData<DeckListViewData>()
    val decksViewData: LiveData<DeckListViewData> = decksViewDataMutableLiveData

    fun loadAllDecks() {
        allLister.listAll { result ->
            decksViewDataMutableLiveData.postValue(result)
        }
    }
}