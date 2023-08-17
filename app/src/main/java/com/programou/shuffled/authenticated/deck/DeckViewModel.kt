package com.programou.shuffled.authenticated.deck

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.programou.shuffled.authenticated.deck.createFlashcard.presentation.CreateFlashcardEvent
import com.programou.shuffled.authenticated.deck.createFlashcard.presentation.CreateFlashcardPresenting
import com.programou.shuffled.authenticated.deck.deleteDeck.presentation.DeleteDeckEvent
import com.programou.shuffled.authenticated.deck.deleteDeck.presentation.DeleteDeckPresenting
import com.programou.shuffled.authenticated.deck.findCards.presenter.FindCardsEvent
import com.programou.shuffled.authenticated.deck.findCards.presenter.FindCardsPresenting
import com.programou.shuffled.authenticated.deck.findCards.presenter.FindCardsViewData
import com.programou.shuffled.authenticated.deck.findDeck.presentation.FindDeckEvent
import com.programou.shuffled.authenticated.deck.findDeck.presentation.FindDeckPresenting
import com.programou.shuffled.authenticated.deck.findDeck.presentation.FindDeckViewData
import com.programou.shuffled.authenticated.deck.updateDeck.presentation.UpdateDeckEvent
import com.programou.shuffled.authenticated.deck.updateDeck.presentation.UpdateDeckPresenting
import com.programou.shuffled.authenticated.deck.updateFavorite.presenter.UpdateFavoriteEvent
import com.programou.shuffled.authenticated.deck.updateFavorite.presenter.UpdateFavoritePresenting
import com.programou.shuffled.authenticated.deckList.Card
import com.programou.shuffled.authenticated.deckList.Deck
import kotlinx.coroutines.launch

data class DeckViewData(val title: String, val description: String, val image: Uri, val isFavorite: Boolean, val cards: List<Card>) {
    data class Card(val id: Int, val question: String, val answer: String, val studiesLeft: Int)
}

class DeckViewModel(
    private val deckId: Int,
    private val createFlashcard: CreateFlashcardPresenting,
    private val findFlashcards: FindCardsPresenting,
    private val deleteDeck: DeleteDeckPresenting,
    private val findDeck: FindDeckPresenting,
    private val updateDeck: UpdateDeckPresenting,
    private val updateFavorite: UpdateFavoritePresenting
): ViewModel() {

    class Factory(
        private val deckId: Int,
        private val createFlashcard: CreateFlashcardPresenting,
        private val findFlashcards: FindCardsPresenting,
        private val deleteDeck: DeleteDeckPresenting,
        private val findDeck: FindDeckPresenting,
        private val updateDeck: UpdateDeckPresenting,
        private val updateFavorite: UpdateFavoritePresenting
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DeckViewModel(
                deckId,
                createFlashcard,
                findFlashcards,
                deleteDeck,
                findDeck, updateDeck, updateFavorite
            ) as T
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
        val findDeckEvent = FindDeckEvent(deckId.toLong())
        val deck = findDeck.findDeck(findDeckEvent)


        val flashcardViewDatas = findFlashcards.findCards(FindCardsEvent(deck.deck.cardIds))
        val deckViewData = deck.toViewData(flashcardViewDatas)
        deckMutableLiveData.postValue(deckViewData)

        if (deckViewData.cards.isEmpty()) {
            onCardListEmptyStateMutableLiveData.postValue(Unit)
        } else {
            onCardListIsNotEmptyMutableLiveData.postValue(deckViewData.cards)
        }
    }

    fun deleteDeck() = viewModelScope.launch {
        val event = DeleteDeckEvent(deckId.toLong())
        deleteDeck.deleteDeck(event)
    }

    fun createCard(newCard: Card) = viewModelScope.launch {
        val event = CreateFlashcardEvent(
            deckId.toLong(),
            CreateFlashcardEvent.Flashcard(newCard.question, newCard.awnser)
        )
        val createFlashcardViewData = createFlashcard.createFlashcard(event)
        loadDeck()
    }

    fun toggleFavorite() = viewModelScope.launch {
        val findDeckEvent = FindDeckEvent(deckId.toLong())
        val deck = findDeck.findDeck(findDeckEvent)

        val updateFavoriteEvent = UpdateFavoriteEvent(deck.deck.deckId, deck.deck.isFavorite.not())
        val updatedViewData = updateFavorite.updateFavorite(updateFavoriteEvent)

        deckIsFavoriteMutableLiveData.postValue(updatedViewData.isFavorite)
    }

    fun changeEditMode() {
        isEditMode = isEditMode.not()
    }

    fun isEditMode() = isEditMode

    fun removeCard(cardId: Int) = viewModelScope.launch {
        val findDeckEvent = FindDeckEvent(deckId.toLong())
        val deck = findDeck.findDeck(findDeckEvent)
        val cardIdsInDeck = deck.deck.cardIds.toMutableList()
        val event = FindCardsEvent(cardIdsInDeck)
        val cards = findFlashcards.findCards(event).cards.toMutableList()
        val index = cards.indexOfFirst { it.id == cardId.toLong() }
        index.let {
            cards.removeAt(it)
        }

        if (cards.isEmpty()) {
            onCardListEmptyStateMutableLiveData.postValue(Unit)
            return@launch
        }

        cards.let {
            val cardsViewData = it.map { card ->
                DeckViewData.Card(card.id.toInt(), card.question, card.answer, card.studiesLeft)
            }
            onCardListIsNotEmptyMutableLiveData.postValue(cardsViewData)
        }
    }

    fun updateCard(cardEdited: Card) = viewModelScope.launch {
        val cardId = cardEdited.id!!.toLong()
        val event = FindCardsEvent(listOf(cardId))
        val cardsViewData = findFlashcards.findCards(event)
        val cardsInDeck = cardsViewData.cards.toMutableList()

        val cardsVd = cardsInDeck.map { card ->
            if (card.id == cardId) {
                return@map DeckViewData.Card(cardId.toInt(), cardEdited.question, cardEdited.awnser, cardEdited.studiesLeft)
            }

            return@map DeckViewData.Card(card.id.toInt(), card.question, card.answer, card.studiesLeft)
        }
        onCardListIsNotEmptyMutableLiveData.postValue(cardsVd)
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
        viewModelScope.launch {
            val newCards = newDeck.cards.map { UpdateDeckEvent.Card(it.id.toLong(), it.question, it.answer, it.studiesLeft) }
            val updateDeckEvent = UpdateDeckEvent(deckId.toLong(), newDeck.title, newDeck.description, newDeck.image.toString(), newCards)
            updateDeck.updateDeck(updateDeckEvent)

            loadDeck()
            changeEditMode()
            onSaveChangeMutableLiveData.postValue(Unit)
        }
    }

    private suspend fun navigateToGame() {
        val findDeckEvent = FindDeckEvent(deckId.toLong())
        val findedDeckViewData = findDeck.findDeck(findDeckEvent)

        val findCardEvents = FindCardsEvent(findedDeckViewData.deck.cardIds)
        val findedCardsViewData = findFlashcards.findCards(findCardEvents)

        val deck = Deck(
            deckId, findedDeckViewData.deck.title,
            findedDeckViewData.deck.description,
            findedDeckViewData.deck.imageUri,
            findedDeckViewData.deck.isFavorite,
            findedCardsViewData.cards.map { card ->
                Card(card.id.toInt(), card.question, card.answer, card.studiesLeft)
            }
        )

        onNavigateToFlashcardStudyMutableLiveData.postValue(deck)
    }
}

fun FindDeckViewData.toViewData(cards: FindCardsViewData): DeckViewData {
    val cardViewDatas = cards.cards.map { card -> DeckViewData.Card(card.id.toInt(), card.question, card.answer, card.studiesLeft) }
    return DeckViewData(
        deck.title,
        deck.description,
        Uri.parse(deck.imageUri)!!,
        deck.isFavorite, cardViewDatas
    )
}
