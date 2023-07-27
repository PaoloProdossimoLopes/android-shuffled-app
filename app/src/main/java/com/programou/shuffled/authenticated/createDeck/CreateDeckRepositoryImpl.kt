package com.programou.shuffled.authenticated.createDeck

typealias CreateDeckClientCompletionBlock = (CreateDeckResponse?) -> Unit
interface CreateDeckClient {
    fun postDeck(deck: CreateDeckModel, onComplete: CreateDeckClientCompletionBlock)
}

class CreateDeckResponse

class CreateDeckRepositoryImpl(private val client: CreateDeckClient): CreateDeckRepository {
    override fun saveDeck(deck: CreateDeckModel, onComplete: (Boolean) -> Unit) {
        client.postDeck(deck) { onComplete(it != null) }
    }
}