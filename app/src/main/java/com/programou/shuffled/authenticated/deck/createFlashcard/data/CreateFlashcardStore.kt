package com.programou.shuffled.authenticated.deck.createFlashcard.data

interface CreateFlashcardStore {
    suspend fun createFlashcard(task: CreateFlashcardTask): Long
    suspend fun updateDeck(task: UpdateFlashcardsInDeckTask)
}