package com.programou.shuffled.authenticated.deck

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.programou.shuffled.authenticated.deckList.Card
import com.programou.shuffled.authenticated.deckList.Deck
import com.programou.shuffled.authenticated.deckList.DeckListResponse
import kotlinx.coroutines.launch

data class DeckViewData(val title: String, val description: String, val image: Uri, val isFavorite: Boolean, val cards: List<Card>) {
    data class Card(val question: String, val answer: String)
}

class DeckViewModel(private val useCase: DeckFinding): ViewModel() {
    private val deckMutableLiveData = MutableLiveData<DeckViewData>()
    val deckLiveData: LiveData<DeckViewData> = deckMutableLiveData

    fun findDeckBy(id: Int) {
        viewModelScope.launch {
            val deck = useCase.findBy(id)
            val deckViewData = DeckViewData(deck.name, deck.description, Uri.parse(deck.thumbnailUrl)!!, deck.isFavorite, deck.cards.map { DeckViewData.Card(it.question, it.awnser) })
            deckMutableLiveData.postValue(deckViewData)
        }
    }
}

interface DeckFinding {
    suspend fun findBy(id: Int): Deck
}

class DeckFinderUseCase(private val repository: DeckRepositoring): DeckFinding {
    override suspend fun findBy(id: Int): Deck = repository.findBy(id)
}

interface DeckRepositoring {
    suspend fun findBy(id: Int): Deck
}

class DeckResponse(val deck: Deck) {
    class Deck(val id: Int, val title: String, val description: String, val thumbnailUrl: String, val isFavorited: Boolean, val cards: List<Card>)
    class Card(val question: String, val answer: String)
}

class DeckRepository(private val client: DeckClienting): DeckRepositoring {
    override suspend fun findBy(id: Int): Deck {
        val response = client.findBy(id)
        with (response!!.deck) {
            return Deck(id, title, description, thumbnailUrl, isFavorited, cards.map { Card(it.question, it.answer) })
        }
    }
}

interface DeckClienting {
    suspend fun findBy(id: Int): DeckResponse?
}


