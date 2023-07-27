package com.programou.shuffled.authenticated.createDeck

interface CreateDeck {
    fun createDeck(deck: CreateDeckModel, onComplete: (Boolean) -> Unit)
}