package com.programou.shuffled.home.presenter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.programou.shuffled.R
import com.programou.shuffled.home.domain.AllDecksLoader
import com.programou.shuffled.home.domain.DeckViewData
import com.programou.shuffled.home.domain.HomeEmptyStateViewData
import com.programou.shuffled.home.domain.HomeErrorStateViewData

class HomeViewModel(
    private val resourceProvider: ResourceProvider,
    private val allDeckLoader: AllDecksLoader
): ViewModel() {
    val displayLoader = MutableLiveData<Boolean>()
    val displayDecks = MutableLiveData<List<DeckViewData>>()
    val displayError = MutableLiveData<HomeErrorStateViewData>()
    val displayEmpty = MutableLiveData<HomeEmptyStateViewData>()

    class Factory(
        private val resourceProvider: ResourceProvider,
        private val allDeckLoader: AllDecksLoader
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(resourceProvider, allDeckLoader) as T
        }
    }

    fun getHomeTitle() = resourceProvider.string(R.string.homeScreenTitleText)

    suspend fun onBacameVisible() {
        displayLoader(true)
        try {
            loadDecks()
        } catch (_: Throwable) {
            displayErrorState()
        }
        displayLoader(false)
    }

    private suspend fun loadDecks() {
        val decks = allDeckLoader.load()
        if (decks.isEmpty())
            return displayEmptyState()
        displayDecks.postValue(decks)
    }

    private fun displayLoader(isLoading: Boolean) {
        displayLoader.postValue(isLoading)
    }

    private fun displayEmptyState() {
        val title = resourceProvider.string(R.string.homeEmptyStateTitleText)
        val message = resourceProvider.string(R.string.homeEmptyStateMessageText)
        val emptyStateViewData = HomeEmptyStateViewData(title, message)
        displayEmpty.postValue(emptyStateViewData)
    }

    private fun displayErrorState() {
        val title = resourceProvider.string(R.string.homeErrorStateTitleText)
        val message = resourceProvider.string(R.string.homeErrorStateMessageText)
        val errorStateViewData = HomeErrorStateViewData(title, message)
        displayError.postValue(errorStateViewData)
    }
}