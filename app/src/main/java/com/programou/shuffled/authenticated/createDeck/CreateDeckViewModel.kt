package com.programou.shuffled.authenticated.createDeck

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CreateDeckViewModel(private val createUseCase: CreateDeck): ViewModel() {
    private val onCompleteMutableLiveData = MutableLiveData<Boolean>()
    val onComplete: LiveData<Boolean> = onCompleteMutableLiveData

    fun create(deck: CreateDeckViewDataRequest) {
        val deckModel = CreateDeckModel(deck.title, deck.description, deck.imageUri)
        createUseCase.createDeck(deckModel) { onSuccess ->
            onCompleteMutableLiveData.postValue(onSuccess)
        }
    }
}