package com.programou.shuffled.authenticated.deck.createFlashcard.domain

interface FlashcardCreator {
    suspend fun createFlashcard(data: CreateFlashcardData): Flashcard

    class InvalidFlashcardError(fieldName: String): Throwable("the field `$fieldName` must not be empty")
    class CreateFlashcardError: Throwable()
}