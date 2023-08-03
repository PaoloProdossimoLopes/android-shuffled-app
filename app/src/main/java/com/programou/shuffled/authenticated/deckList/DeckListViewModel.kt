package com.programou.shuffled.authenticated.deckList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.programou.shuffled.R
import com.programou.shuffled.authenticated.deckList.findAllDecks.presentation.FindAllDecksPresenting
import kotlinx.coroutines.launch

class DeckListViewModel(private val allLister: FindAllDecksPresenting): ViewModel() {

    class Factory(private val allLister: FindAllDecksPresenting): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DeckListViewModel(allLister) as T
        }
    }

    private val decksViewDataMutableLiveData = MutableLiveData<DeckListViewData>()
    val decksViewData: LiveData<DeckListViewData> = decksViewDataMutableLiveData

    fun loadAllDecks() = viewModelScope.launch {
        val deckViewDatas = allLister.findAll()

        if (deckViewDatas.decks.isEmpty()) {
            val viewData = DeckListViewData.empty(DeckListViewData.Empty(R.drawable.ic_no_layers, "Sua lsita de baralhos esta vazia", "Crie um baralho e começe seus estudos"))
            decksViewDataMutableLiveData.postValue(viewData)
            return@launch
        }

        decksViewDataMutableLiveData.postValue(DeckListViewData.decks(deckViewDatas.decks.map {
            DeckListViewData.Deck(it.id.toInt(), it.title, it.totalCards, it.imageUri)
        }))
    }
}