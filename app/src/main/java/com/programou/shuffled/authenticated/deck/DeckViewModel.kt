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

class DeckViewModel(private val findUseCase: DeckFinding, private val updateUseCase: DeckUpdating): ViewModel() {
    private val deckMutableLiveData = MutableLiveData<DeckViewData>()
    val deckLiveData: LiveData<DeckViewData> = deckMutableLiveData

    fun findDeckBy(id: Int) {
        viewModelScope.launch {
            val deck = findUseCase.findBy(id)
            val deckViewData = DeckViewData(deck.name, deck.description, Uri.parse(deck.thumbnailUrl)!!, deck.isFavorite, deck.cards.map { DeckViewData.Card(it.id!!, it.question, it.awnser) })
            deckMutableLiveData.postValue(deckViewData)
        }
    }

    fun updateDeck(deck: Deck) = viewModelScope.launch {
        updateUseCase.updateDeck(deck)
    }
    fun createCard(deckId: Int, newCard: Card) = viewModelScope.launch {
        updateUseCase.createCard(deckId, newCard)
    }
    fun updateFavorite(deckId: Int, isFavorited: Boolean) = viewModelScope.launch {
        updateUseCase.updateFavorited(deckId, isFavorited)
    }
}

interface DeckFinding {
    suspend fun findBy(id: Int): Deck
}

interface DeckUpdating {
    suspend fun updateDeck(deck: Deck): Boolean
    suspend fun createCard(deckId: Int, newCard: Card): Boolean
    suspend fun updateFavorited(deckId: Int, isFavorited: Boolean): Boolean
}

class DeckFinderUseCase(private val repository: DeckRepositoring): DeckFinding {
    override suspend fun findBy(id: Int): Deck = repository.findBy(id)
}

class DeckUpdate(private val repository: DeckUpdateRepositing): DeckUpdating {
    override suspend fun updateDeck(deck: Deck) = repository.updateDeck(deck)
    override suspend fun createCard(deckId: Int, newCard: Card) = repository.createCard(deckId, newCard)

    override suspend fun updateFavorited(deckId: Int, isFavorited: Boolean) = repository.updateFavorited(deckId, isFavorited)
}

interface DeckUpdateRepositing {
    suspend fun updateDeck(deck: Deck): Boolean
    suspend fun createCard(deckId: Int, newCard: Card): Boolean
    suspend fun updateFavorited(deckId: Int, isFavorited: Boolean): Boolean
}

interface DeckRepositoring {
    suspend fun findBy(id: Int): Deck
}

class DeckResponse(val deck: Deck) {
    class Deck(val id: Int, val title: String, val description: String, val thumbnailUrl: String, val isFavorited: Boolean, val cards: List<Card>)
    class Card(val id: Int, val question: String, val answer: String)
}

class DeckRepository(private val client: DeckClienting): DeckRepositoring {
    override suspend fun findBy(id: Int): Deck {
        val response = client.findBy(id)
        with (response!!.deck) {
            return Deck(id, title, description, thumbnailUrl, isFavorited, cards.map { Card(it.id, it.question, it.answer) })
        }
    }
}

class DeckUpdateRepository(private val client: DeckUpdateClienting): DeckUpdateRepositing {
    override suspend fun updateDeck(deck: Deck) = client.updateDeck(deck)
    override suspend fun createCard(deckId: Int, newCard: Card) = client.createCard(deckId, newCard)

    override suspend fun updateFavorited(deckId: Int, isFavorited: Boolean) = client.updateFavorited(deckId, isFavorited)
}


interface DeckClienting {
    suspend fun findBy(id: Int): DeckResponse?
}

interface DeckUpdateClienting {
    suspend fun updateDeck(deck: Deck): Boolean
    suspend fun createCard(deckId: Int, newCard: Card): Boolean
    suspend fun updateFavorited(deckId: Int, isFavorited: Boolean): Boolean
}


