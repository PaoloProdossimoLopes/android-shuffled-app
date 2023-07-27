package com.programou.shuffled.authenticated.createDeck

interface CreateDeckRepository {
    fun saveDeck(deck: CreateDeckModel, onComplete: (Boolean) -> Unit)
}