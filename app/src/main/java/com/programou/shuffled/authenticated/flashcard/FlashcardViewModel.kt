package com.programou.shuffled.authenticated.flashcard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.programou.shuffled.authenticated.deckList.Deck
import java.util.Dictionary


class FlashcardViewModel(private val deck: Deck, private val client: FlashcardClient): ViewModel() {

    private val onItemsChangeMutableLiveData = MutableLiveData<List<FlashCardViewData>>()
    val onItemsChange: LiveData<List<FlashCardViewData>> = onItemsChangeMutableLiveData

    private val onEasySelectChangeMutableLiveData = MutableLiveData<Unit>()
    val onEasySelectChange: LiveData<Unit> = onEasySelectChangeMutableLiveData

    private val onIntermediateSelectChangeMutableLiveData = MutableLiveData<Unit>()
    val onIntermediateSelect: LiveData<Unit> = onIntermediateSelectChangeMutableLiveData

    private val onHardSelectChangeMutableLiveData = MutableLiveData<Unit>()
    val onHardSelectChange: LiveData<Unit> = onHardSelectChangeMutableLiveData

    private val onDisableButtonsChangeMutableLiveData = MutableLiveData<Unit>()
    val onDisableButtonsChange: LiveData<Unit> = onDisableButtonsChangeMutableLiveData

    private val onUpdateStepChangeMutableLiveData = MutableLiveData<FlashcardStep>()
    val onUpdateStepChange: LiveData<FlashcardStep> = onUpdateStepChangeMutableLiveData

    private val onNavigateToResultMutableLiveData = MutableLiveData<FlashcardResult>()
    val onNavigateToResult: LiveData<FlashcardResult> = onNavigateToResultMutableLiveData

    private var cardMarkedAsEasyCounter = mutableListOf<Int>()
    private var cardMarkedAsIntermediateCounter = mutableListOf<Int>()
    private var cardMarkedAsHardCounter = mutableListOf<Int>()
    private var currentFlashCardPositionSelected = 0

    fun getCards() = deck.cards
        .filter { it.studiesLeft == 0 }
        .map {
            FlashCardViewData(it.id!!, it.question, it.awnser)
        }

    fun presentCards() {
        onItemsChangeMutableLiveData.postValue(getCards())
    }

    fun selectEasy() {
        onEasySelectChangeMutableLiveData.postValue(Unit)
        cardMarkedAsEasyCounter.add(getCurrentCardId())
        moveToNext()
        onDisableButtonsChangeMutableLiveData.postValue(Unit)
    }

    private fun getCurrentCardId() = deck.cards[currentFlashCardPositionSelected].id!!

    fun selectIntermediate() {
        onIntermediateSelectChangeMutableLiveData.postValue(Unit)
        cardMarkedAsIntermediateCounter.add(getCurrentCardId())
        moveToNext()
        onDisableButtonsChangeMutableLiveData.postValue(Unit)
    }

    fun selectHard() {
        onHardSelectChangeMutableLiveData.postValue(Unit)
        cardMarkedAsHardCounter.add(getCurrentCardId())
        moveToNext()
        onDisableButtonsChangeMutableLiveData.postValue(Unit)
    }

    fun getProgress() = (((currentFlashCardPositionSelected + 1) * 100) / getCards().count())

    private fun moveToNext() {
        if (currentFlashCardPositionSelected + 1 < getCards().count()) {
            currentFlashCardPositionSelected++

            updateStep()
        } else {
            client.updateDecrementStudiesLeftFor(deck.id)
            client.updateStudiesLeftsFor(cardMarkedAsEasyCounter, deck.id, 2)
            client.updateStudiesLeftsFor(cardMarkedAsIntermediateCounter, deck.id, 1)
            client.updateStudiesLeftsFor(cardMarkedAsHardCounter, deck.id, 0)
            onNavigateToResultMutableLiveData.postValue(FlashcardResult(
                deck.name, getCards().count(),
                cardMarkedAsEasyCounter.count(),
                cardMarkedAsIntermediateCounter.count(),
                cardMarkedAsHardCounter.count(),
            ))
        }
    }

    private fun updateStep() {
        val currentStep = currentFlashCardPositionSelected + 1
        val update = FlashcardStep(getProgress(), currentStep.toString(), currentFlashCardPositionSelected)
        onUpdateStepChangeMutableLiveData.postValue(update)
    }
}

interface FlashcardClient {
    fun updateStudiesLeftsFor(idCards: List<Int>, inDeckId: Int, studiesLeft: Int)
    fun updateDecrementStudiesLeftFor(deckId: Int)
}