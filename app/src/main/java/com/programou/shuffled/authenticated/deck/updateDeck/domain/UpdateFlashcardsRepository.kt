package com.programou.shuffled.authenticated.deck.updateDeck.domain

interface UpdateFlashcardsRepository {
    suspend fun update(request: UpdateFlashcardsRequest): List<Flashcard>
}