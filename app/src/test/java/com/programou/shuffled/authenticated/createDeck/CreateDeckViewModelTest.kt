package com.programou.shuffled.authenticated.createDeck

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class CreateDeckViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `test on 'create' not complete 'onComplete' observer until use case complets`() {
        val onCompleteObserver: Observer<Boolean> = mock()
        val deck = CreateDeckViewDataRequest("any title", "any description", "any image uri")
        val sut = CreateDeckViewModel(mock())
        sut.onComplete.observeForever(onCompleteObserver)

        sut.create(deck)

        verify(onCompleteObserver, times(0)).onChanged(any())
    }

    @Test
    fun `test on 'create' calls use case method with correct params`() {
        val deckViewData = CreateDeckViewDataRequest("any title", "any description", "any image uri")
        val deckModel = CreateDeckModel(deckViewData.title, deckViewData.description, deckViewData.imageUri)
        val createDeckUseCaseArgument = argumentCaptor<(Boolean) -> Unit>()
        val useCase = mock<CreateDeck> {
            on { createDeck(eq(deckModel), createDeckUseCaseArgument.capture()) } doAnswer {
                createDeckUseCaseArgument.firstValue.invoke(true)
            }
        }
        val sut = CreateDeckViewModel(useCase)

        sut.create(deckViewData)

        verify(useCase, times(1)).createDeck(eq(deckModel), any())
    }

    @Test
    fun `test on 'create' triggers 'onComplete' observer with FALSE after use case response with FALSE`() {
        val onCompleteObserver: Observer<Boolean> = mock()
        val deckViewData = CreateDeckViewDataRequest("any title", "any description", "any image uri")
        val deckModel = CreateDeckModel(deckViewData.title, deckViewData.description, deckViewData.imageUri)
        val createDeckUseCaseArgument = argumentCaptor<(Boolean) -> Unit>()
        val createDeckUseCaseArguemntResponse = false
        val useCase = mock<CreateDeck> {
            on { createDeck(eq(deckModel), createDeckUseCaseArgument.capture()) } doAnswer {
                createDeckUseCaseArgument.firstValue.invoke(createDeckUseCaseArguemntResponse)
            }
        }
        val sut = CreateDeckViewModel(useCase)
        sut.onComplete.observeForever(onCompleteObserver)

        sut.create(deckViewData)

        verify(onCompleteObserver, times(1)).onChanged(eq(createDeckUseCaseArguemntResponse))
    }

    @Test
    fun `test on 'create' triggers 'onComplete' observer with TRUE after use case response TRUE`() {
        val onCompleteObserver: Observer<Boolean> = mock()
        val deckViewData = CreateDeckViewDataRequest("any title", "any description", "any image uri")
        val deckModel = CreateDeckModel(deckViewData.title, deckViewData.description, deckViewData.imageUri)
        val createDeckUseCaseArgument = argumentCaptor<(Boolean) -> Unit>()
        val createDeckUseCaseArguemntResponse = true
        val useCase = mock<CreateDeck> {
            on { createDeck(eq(deckModel), createDeckUseCaseArgument.capture()) } doAnswer {
                createDeckUseCaseArgument.firstValue.invoke(createDeckUseCaseArguemntResponse)
            }
        }
        val sut = CreateDeckViewModel(useCase)
        sut.onComplete.observeForever(onCompleteObserver)

        sut.create(deckViewData)

        verify(onCompleteObserver, times(1)).onChanged(eq(createDeckUseCaseArguemntResponse))
    }
}