package com.programou.shuffled.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.programou.shuffled.R
import com.programou.shuffled.home.domain.AllDecksLoader
import com.programou.shuffled.home.domain.DeckViewData
import com.programou.shuffled.home.domain.HomeEmptyStateViewData
import com.programou.shuffled.home.domain.HomeErrorStateViewData
import com.programou.shuffled.home.presenter.HomeViewModel
import com.programou.shuffled.home.presenter.ResourceProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class HomeViewModelTests {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `on became visible should display and hides the loader`() = runBlocking {
        val sut = HomeViewModel(stubResourceProviderDummy(), mock())
        val displaLoaderStateSpies = mutableListOf<Boolean>()
        sut.displayLoader.observeForever { displaLoaderStateSpies.add(it) }
        sut.onBacameVisible()
        Assert.assertEquals(displaLoaderStateSpies, listOf(true, false))
    }

    @Test
    fun `on get home title property should request string once`() = runBlocking {
        val homeTitleResouce = "any home title"
        val sut = HomeViewModel(ResourceProviderFake(listOf(homeTitleResouce)), mock())
        val title = sut.getHomeTitle()
        Assert.assertEquals(title, homeTitleResouce)
    }

    @Test
    fun `on get home title property should passing the correct resource id`() = runBlocking {
        val resourceProvider = stubResourceProviderDummy()
        val sut = HomeViewModel(resourceProvider, mock())
        sut.getHomeTitle()
        verify(resourceProvider, times(1)).string(R.string.homeScreenTitleText)
        return@runBlocking
    }

    @Test
    fun `on became visible should display and hides the loader when use case throws` () = runBlocking {
        val sut = HomeViewModel(stubResourceProviderDummy(), stubLoadDeckLoaderWithError())
        val displayLoaderStateSpies = mutableListOf<Boolean>()
        sut.displayLoader.observeForever { displayLoaderStateSpies.add(it) }
        sut.onBacameVisible()
        Assert.assertEquals(displayLoaderStateSpies, listOf(true, false))
    }

    @Test
    fun `on became visible should display error state with view data`() = runBlocking {
        val resources = listOf("any error state valid title", "any error state valid message")
        val sut = HomeViewModel(ResourceProviderFake(resources), stubLoadDeckLoaderWithError())
        val displayErrorSpy = mutableListOf<HomeErrorStateViewData>()
        sut.displayError.observeForever { displayErrorSpy.add(it) }
        sut.onBacameVisible()
        Assert.assertEquals(displayErrorSpy, listOf(HomeErrorStateViewData(resources[0], resources[1])))
    }

    @Test
    fun `on became visible should call load method from use case once`() = runBlocking {
        val decksLoader: AllDecksLoader = stubLoadDecksLoader()
        val sut = HomeViewModel(stubResourceProviderDummy(), decksLoader)
        val displayLoaderStateSpy = mutableListOf<Boolean>()
        sut.displayLoader.observeForever { displayLoaderStateSpy.add(it) }
        sut.onBacameVisible()
        verify(decksLoader, times(1)).load()
        return@runBlocking
    }

    @Test
    fun `on became visible should display empty state when use case delievers an empty list`() = runBlocking {
        val resources = listOf("any empty state valid title", "any empty state valid message")
        val sut = HomeViewModel(ResourceProviderFake(resources), stubLoadDecksLoader())
        val displayDecksSpy = mutableListOf<List<DeckViewData>>()
        val displayEmptySpy = mutableListOf<HomeEmptyStateViewData>()
        sut.displayDecks.observeForever { displayDecksSpy.add(it) }
        sut.displayEmpty.observeForever { displayEmptySpy.add(it) }
        sut.onBacameVisible()
        Assert.assertEquals(displayDecksSpy.count(), 0)
        Assert.assertEquals(displayEmptySpy, listOf(HomeEmptyStateViewData(resources[0], resources[1])))
    }

    @Test
    fun `on became visible should get string twice for title and message in empty state`() = runBlocking {
        val resourceProvider = stubResourceProviderDummy()
        val sut = HomeViewModel(resourceProvider, stubLoadDecksLoader(listOf()))
        sut.onBacameVisible()
        verify(resourceProvider, times(2)).string(any())
        verify(resourceProvider, times(1)).string(R.string.homeEmptyStateMessageText)
        verify(resourceProvider, times(1)).string(R.string.homeEmptyStateTitleText)
        return@runBlocking
    }

    @Test
    fun `on became visible should get string twice for title and message in error state`() = runBlocking {
        val resourceProvider = stubResourceProviderDummy()
        val sut = HomeViewModel(resourceProvider, stubLoadDeckLoaderWithError())
        sut.onBacameVisible()
        verify(resourceProvider, times(2)).string(any())
        verify(resourceProvider, times(1)).string(R.string.homeErrorStateMessageText)
        verify(resourceProvider, times(1)).string(R.string.homeErrorStateTitleText)
        return@runBlocking
    }

    @Test
    fun `on became visible should display decks when use case deleivers a non empty list`() = runBlocking {
        val decks = listOf(makeDeck())
        val decksLoader = stubLoadDecksLoader(decks)
        val sut = HomeViewModel(mock(), decksLoader)
        val displayDecksSpy = mutableListOf<List<DeckViewData>>()
        val displayEmptySpy = mutableListOf<HomeEmptyStateViewData>()
        sut.displayDecks.observeForever { displayDecksSpy.add(it) }
        sut.displayEmpty.observeForever { displayEmptySpy.add(it) }
        sut.onBacameVisible()
        Assert.assertEquals(displayDecksSpy, listOf(decks))
        Assert.assertEquals(displayEmptySpy, listOf<HomeEmptyStateViewData>())
    }

    private fun stubLoadDecksLoader(decks: List<DeckViewData> = listOf()): AllDecksLoader = mock {
        onBlocking { load() } doReturn (decks)
    }

    private fun stubLoadDeckLoaderWithError() = mock<AllDecksLoader> {
        onBlocking { load() } doAnswer { throw Error() }
    }

    private fun stubResourceProviderDummy() = mock<ResourceProvider>() {
        on { string(any()) } doReturn String()
    }

    private fun makeDeck() = DeckViewData("any deck name", "any number", "any valid url")

    class ResourceProviderFake(private val strings: List<String>): ResourceProvider {
        private var index = 0
        override fun string(id: Int): String {
            val string = strings[index]
            index++
            return string
        }
    }
}