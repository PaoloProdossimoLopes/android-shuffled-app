package com.programou.shuffled.authenticated.deck.createFlashcard.domain

interface CreateFlashcardRepository {
    suspend fun createFlashcard(request: CreateFlashcardRequest): Long
}

