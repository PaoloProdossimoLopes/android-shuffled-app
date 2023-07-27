package com.programou.shuffled.authenticated.createDeck

class CreateDeckUseCase(private val repository: CreateDeckRepository): CreateDeck {
    override fun createDeck(deck: CreateDeckModel, onComplete: (Boolean) -> Unit) {
        try {
            repository.saveDeck(deck, onComplete)
        } catch (e: Throwable) {
            onComplete(false)
        }
    }
}