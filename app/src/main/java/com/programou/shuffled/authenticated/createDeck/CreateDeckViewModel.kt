package com.programou.shuffled.authenticated.createDeck

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CreateDeckViewModel(private val createUseCase: CreateDeck): ViewModel() {
    class Factory(private val createUseCase: CreateDeck): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CreateDeckViewModel(createUseCase) as T
        }
    }

    private val onCompleteMutableLiveData = MutableLiveData<Boolean>()
    val onComplete: LiveData<Boolean> = onCompleteMutableLiveData
    fun create(deck: CreateDeckViewDataRequest) {
        val deckModel = CreateDeckModel(deck.title, deck.description, deck.imageUri, deck.isFavorited, deck.cards.map { CreateDeckModel.Card(it.id, it.question, it.awnser, it.studiesLeft) })
        createUseCase.createDeck(deckModel) { onSuccess ->
            onCompleteMutableLiveData.postValue(onSuccess)
        }
    }
}