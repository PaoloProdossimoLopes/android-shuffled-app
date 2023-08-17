package com.programou.shuffled.authenticated.deck.createFlashcard.presentation

interface CreateFlashcardPresenting {
    suspend fun createFlashcard(event: CreateFlashcardEvent): CreateFlashcardViewData
}