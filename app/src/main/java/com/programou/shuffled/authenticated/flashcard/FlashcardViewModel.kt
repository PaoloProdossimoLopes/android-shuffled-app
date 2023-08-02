package com.programou.shuffled.authenticated.flashcard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.programou.shuffled.authenticated.deckList.Deck


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

    private var listOfIdCardsMarkedAsEasy = mutableListOf<Int>()
    private var listOfIdCardsMarkedAsIntermediate = mutableListOf<Int>()
    private var listOfIdCardsMarkedAsHard = mutableListOf<Int>()
    private var currentFlashCardPositionSelected = 0

    fun getCards(): List<FlashCardViewData> {
        val lowerCardAvailable = deck.cards.minBy { it.studiesLeft }
        return deck.cards
            .filter { it.studiesLeft == lowerCardAvailable.studiesLeft }
            .map {
                FlashCardViewData(it.id!!, it.question, it.awnser)
            }
    }

    fun presentCards() {
        onItemsChangeMutableLiveData.postValue(getCards())
    }

    fun selectEasy() {
        onEasySelectChangeMutableLiveData.postValue(Unit)
        listOfIdCardsMarkedAsEasy.add(getCurrentCardId())
        moveToNext()
        onDisableButtonsChangeMutableLiveData.postValue(Unit)
    }

    private fun getCurrentCardId() = deck.cards[currentFlashCardPositionSelected].id!!

    fun selectIntermediate() {
        onIntermediateSelectChangeMutableLiveData.postValue(Unit)
        listOfIdCardsMarkedAsIntermediate.add(getCurrentCardId())
        moveToNext()
        onDisableButtonsChangeMutableLiveData.postValue(Unit)
    }

    fun selectHard() {
        onHardSelectChangeMutableLiveData.postValue(Unit)
        listOfIdCardsMarkedAsHard.add(getCurrentCardId())
        moveToNext()
        onDisableButtonsChangeMutableLiveData.postValue(Unit)
    }

    fun getProgress() = (((currentFlashCardPositionSelected + 1) * 100) / getCards().count())

    private fun moveToNext() {
        if (currentFlashCardPositionSelected + 1 < getCards().count()) {
            currentFlashCardPositionSelected++

            updateStep()
        } else {
            val cards = mutableListOf<CardStudieUpdate>()
            for (card in deck.cards) {
                if (listOfIdCardsMarkedAsEasy.contains(card.id)) {
                    cards.add(CardStudieUpdate(card.id!!.toLong(), 2))
                    continue
                }

                if (listOfIdCardsMarkedAsIntermediate.contains(card.id)) {
                    cards.add(CardStudieUpdate(card.id!!.toLong(), 1))
                    continue
                }

                if (listOfIdCardsMarkedAsIntermediate.contains(card.id)) {
                    cards.add(CardStudieUpdate(card.id!!.toLong(), 0))
                    continue
                }

                if (card.studiesLeft < 0) {
                    cards.add(CardStudieUpdate(card.id!!.toLong(), 0))
                    continue
                }

                cards.add(CardStudieUpdate(card.id!!.toLong(), card.studiesLeft - 1))
            }
            client.updateStudiesLeftsFor(cards)

            onNavigateToResultMutableLiveData.postValue(FlashcardResult(
                deck.name, getCards().count(),
                listOfIdCardsMarkedAsEasy.count(),
                listOfIdCardsMarkedAsIntermediate.count(),
                listOfIdCardsMarkedAsHard.count(),
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
    fun updateStudiesLeftsFor(cards: List<CardStudieUpdate>)
}

data class CardStudieUpdate(
    val cardId: Long,
    val studiesLeft: Int
)