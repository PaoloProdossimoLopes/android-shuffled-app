package com.programou.shuffled.authenticated.deck

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.programou.shuffled.authenticated.deckList.Card
import com.programou.shuffled.authenticated.deckList.Deck
import kotlinx.coroutines.launch

data class DeckViewData(val title: String, val description: String, val image: Uri, val isFavorite: Boolean, val cards: List<Card>) {
    data class Card(val id: Int, val question: String, val answer: String)
}

class DeckViewModel(private val findClient: DeckClienting, private val updateClient: DeckUpdateClienting): ViewModel() {
    private val deckMutableLiveData = MutableLiveData<DeckViewData>()
    val deckLiveData: LiveData<DeckViewData> = deckMutableLiveData

    fun findDeckBy(id: Int) {
        viewModelScope.launch {
            val deck = findClient.findBy(id)
            deck?.let {
                val deckViewData = DeckViewData(it.deck.title, it.deck.description, Uri.parse(it.deck.thumbnailUrl)!!, it.deck.isFavorited, it.deck.cards.map { card -> DeckViewData.Card(card.id, card.question, card.answer) })
                deckMutableLiveData.postValue(deckViewData)
            }
        }
    }

    fun deleteDeck(id: Int) = viewModelScope.launch {
        updateClient.deleteDeck(id)
    }

    fun updateDeck(deck: Deck) = viewModelScope.launch {
        updateClient.updateDeck(deck)
    }
    fun createCard(deckId: Int, newCard: Card) = viewModelScope.launch {
        updateClient.createCard(deckId, newCard)
    }
    fun updateFavorite(deckId: Int, isFavorited: Boolean) = viewModelScope.launch {
        updateClient.updateFavorited(deckId, isFavorited)
    }
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
    class Card(val id: Int, val question: String, val answer: String)
}


