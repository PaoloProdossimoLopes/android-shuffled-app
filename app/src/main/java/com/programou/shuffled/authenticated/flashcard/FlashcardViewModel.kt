package com.programou.shuffled.authenticated.flashcard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.programou.shuffled.authenticated.deckList.Deck


class FlashcardViewModel(private val deck: Deck): ViewModel() {

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

    private var cardMarkedAsEasyCounter = 0
    private var cardMarkedAsIntermediateCounter = 0
    private var cardMarkedAsHardCounter = 0
    private var currentFlashCardPositionSelected = 0

    fun presentCards() {
        val cards = deck.cards.map {
            FlashCardViewData(it.id!!, it.question, it.awnser)
        }

        onItemsChangeMutableLiveData.postValue(cards)
    }

    fun selectEasy() {
        onEasySelectChangeMutableLiveData.postValue(Unit)
        cardMarkedAsEasyCounter++
        moveToNext()
        onDisableButtonsChangeMutableLiveData.postValue(Unit)
    }

    fun selectIntermediate() {
        onIntermediateSelectChangeMutableLiveData.postValue(Unit)
        cardMarkedAsIntermediateCounter++
        moveToNext()
        onDisableButtonsChangeMutableLiveData.postValue(Unit)
    }

    fun selectHard() {
        onHardSelectChangeMutableLiveData.postValue(Unit)
        cardMarkedAsHardCounter++
        moveToNext()
        onDisableButtonsChangeMutableLiveData.postValue(Unit)
    }

    fun getProgress() = (((currentFlashCardPositionSelected + 1) * 100) / deck.cards.count())

    private fun moveToNext() {
        if (currentFlashCardPositionSelected + 1 < deck.cards.count()) {
            currentFlashCardPositionSelected++

            updateStep()
        } else {
            onNavigateToResultMutableLiveData.postValue(FlashcardResult(
                deck.name, deck.cards.count(),
                cardMarkedAsEasyCounter,
                cardMarkedAsIntermediateCounter,
                cardMarkedAsHardCounter,
            ))
        }
    }

    private fun updateStep() {
        val currentStep = currentFlashCardPositionSelected + 1
        val update = FlashcardStep(getProgress(), currentStep.toString(), currentFlashCardPositionSelected)
        onUpdateStepChangeMutableLiveData.postValue(update)
    }
}