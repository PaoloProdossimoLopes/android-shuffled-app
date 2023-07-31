package com.programou.shuffled.authenticated.deck

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.fragment.findNavController
import com.programou.shuffled.authenticated.deckList.Card
import com.programou.shuffled.authenticated.deckList.Deck
import kotlinx.coroutines.launch

data class DeckViewData(val title: String, val description: String, val image: Uri, val isFavorite: Boolean, val cards: List<Card>) {
    data class Card(val id: Int, val question: String, val answer: String, val studiesLeft: Int)
}

class DeckViewModel(
    private val deckId: Int,
    private val findClient: DeckClienting,
    private val updateClient: DeckUpdateClienting
): ViewModel() {

    class Factory(
        private val deckId: Int,
        private val findClient: DeckClienting,
        private val updateClient: DeckUpdateClienting
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DeckViewModel(deckId, findClient, updateClient) as T
        }
    }

    private val deckMutableLiveData = MutableLiveData<DeckViewData>()
    val deckLiveData: LiveData<DeckViewData> = deckMutableLiveData

    private val deckIsFavoriteMutableLiveData = MutableLiveData<Boolean>()
    val deckIsFavorite: LiveData<Boolean> = deckIsFavoriteMutableLiveData

    private val onCardListEmptyStateMutableLiveData = MutableLiveData<Unit>()
    val onCardListEmptyState: LiveData<Unit> = onCardListEmptyStateMutableLiveData

    private val onCardListIsNotEmptyMutableLiveData = MutableLiveData<List<DeckViewData.Card>>()
    val onCardListIsNotEmpty: LiveData<List<DeckViewData.Card>> = onCardListIsNotEmptyMutableLiveData

    private val onEnableEditModeMutableLiveData = MutableLiveData<Unit>()
    val onEnableEditMode: LiveData<Unit> = onEnableEditModeMutableLiveData

    private val onDisableEditModeMutableLiveData = MutableLiveData<Unit>()
    val onDisableEditMode: LiveData<Unit> = onDisableEditModeMutableLiveData

    private val onNavigateToFlashcardStudyMutableLiveData = MutableLiveData<Deck>()
    val onNavigateToFlashcardStudy: LiveData<Deck> = onNavigateToFlashcardStudyMutableLiveData

    private val onSaveChangeMutableLiveData = MutableLiveData<Unit>()
    val onSaveChange: LiveData<Unit> = onSaveChangeMutableLiveData

    private val onPresentGalleryPickerMutableLiveData = MutableLiveData<Unit>()
    val onPresentGalleryPicker: LiveData<Unit> = onPresentGalleryPickerMutableLiveData

    private var isEditMode = false
        set(value) {
            field = value

            if (field) {
                onEnableEditModeMutableLiveData.postValue(Unit)
            } else {
                onDisableEditModeMutableLiveData.postValue(Unit)
            }
        }

    fun loadDeck() = viewModelScope.launch {
        val deck = findClient.findBy(deckId)
        deck?.let {
            val deckViewData = it.toViewData()
            deckMutableLiveData.postValue(deckViewData)

            if (deckViewData.cards.isEmpty()) {
                onCardListEmptyStateMutableLiveData.postValue(Unit)
            } else {
                onCardListIsNotEmptyMutableLiveData.postValue(deckViewData.cards)
            }
        }
    }

    fun deleteDeck() = viewModelScope.launch {
        updateClient.deleteDeck(deckId)
    }

    private fun updateDeck(deck: Deck) = viewModelScope.launch {
        updateClient.updateDeck(deck)
    }
    fun createCard(newCard: Card) = viewModelScope.launch {
        updateClient.createCard(deckId, newCard)
        loadDeck()
    }

    fun toggleFavorite() = viewModelScope.launch {
        val deck = findClient.findBy(deckId)
        deck?.deck?.isFavorited?.let { currentFavoritedState ->
            val newFavoritedState = currentFavoritedState.not()
            updateClient.updateFavorited(deckId, newFavoritedState)
            deckIsFavoriteMutableLiveData.postValue(newFavoritedState)
        }
    }

    fun changeEditMode() {
        isEditMode = isEditMode.not()
    }

    fun isEditMode() = isEditMode

    fun removeCard(cardId: Int) = viewModelScope.launch {
        val deck = findClient.findBy(deckId)
        val cardsInDeck = deck?.deck?.cards?.toMutableList()
        val index = cardsInDeck?.indexOfFirst { it.id == cardId }
        index?.let {
            cardsInDeck?.removeAt(it)
        }

        if (cardsInDeck?.isEmpty() == true) {
            onCardListEmptyStateMutableLiveData.postValue(Unit)
            return@launch
        }

        cardsInDeck?.let {
            val cardsViewData = it.map { card ->
                DeckViewData.Card(card.id, card.question, card.answer, card.studiesLeft)
            }
            onCardListIsNotEmptyMutableLiveData.postValue(cardsViewData)
        }
    }

    fun updateCard(cardEdited: Card) = viewModelScope.launch {
        val deck = findClient.findBy(deckId)
        val cardsInDeck = deck?.deck?.cards?.toMutableList()

        cardsInDeck?.let { allCards ->
            val cardsViewData = allCards.map { card ->
                if (card.id == cardEdited.id) {
                    return@map DeckViewData.Card(card.id, cardEdited.question, cardEdited.awnser, cardEdited.studiesLeft)
                }

                return@map DeckViewData.Card(card.id, card.question, card.answer, card.studiesLeft)
            }
            onCardListIsNotEmptyMutableLiveData.postValue(cardsViewData)
        }
    }

    fun studyOrSave(deckUpdated: DeckViewData) = viewModelScope.launch {
        if (isEditMode) {
            updateDeck(deckUpdated)
        } else {
            navigateToGame()
        }
    }

    fun selectDeckImage() {
        if (isEditMode) {
            onPresentGalleryPickerMutableLiveData.postValue(Unit)
        }
    }

    private fun updateDeck(newDeck: DeckViewData) {
        val deck = Deck(
            deckId, newDeck.title, newDeck.description,
            newDeck.image.toString(), newDeck.isFavorite,
            newDeck.cards.map { Card(it.id, it.question, it.answer, it.studiesLeft) }
        )
        updateDeck(deck)
        loadDeck()
        changeEditMode()
        onSaveChangeMutableLiveData.postValue(Unit)
    }

    private suspend fun navigateToGame() {
        val findedDeck = findClient.findBy(deckId)?.deck
        val cards = findedDeck?.cards?.map { card ->
            Card(card.id, card.question, card.answer, card.studiesLeft)
        } ?: listOf()
        val deck = Deck(
            deckId, findedDeck?.title.toString(),
            findedDeck?.description.toString(),
            findedDeck?.thumbnailUrl.toString(),
            findedDeck?.isFavorited == true,
            cards
        )
        onNavigateToFlashcardStudyMutableLiveData.postValue(deck)
    }
}

fun DeckResponse.toViewData(): DeckViewData {
    val cards = deck.cards.map { card -> DeckViewData.Card(card.id, card.question, card.answer, card.studiesLeft) }
    return DeckViewData(
        deck.title,
        deck.description,
        Uri.parse(deck.thumbnailUrl)!!,
        deck.isFavorited, cards
    )
}

interface DeckClienting {
    suspend fun findBy(id: Int): DeckResponse?
}

interface DeckUpdateClienting {
    suspend fun updateDeck(deck: Deck): Boolean
    suspend fun createCard(deckId: Int, newCard: Card): Boolean
    suspend fun updateFavorited(deckId: Int, isFavorited: Boolean): Boolean
    suspend fun deleteDeck(id: Int): Boolean
}

class DeckResponse(val deck: Deck) {
    class Deck(val id: Int, val title: String, val description: String, val thumbnailUrl: String, val isFavorited: Boolean, val cards: MutableList<Card>)
    class Card(val id: Int, val question: String, val answer: String, val studiesLeft: Int)
}


