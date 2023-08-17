package com.programou.shuffled.authenticated.deck.deleteDeck.domain

import com.programou.shuffled.authenticated.deck.findDeck.domain.FindDeckRepository
import com.programou.shuffled.authenticated.deck.findDeck.domain.FindDeckRequest

class DeleteDeck(
    private val findDeckRespository: FindDeckRepository,
    private val deleteDeckRepository: DeleteDeckRepository,
    private val deleteCardRepository: DeleteCardsRepository
): DeckDeletor {

    override suspend fun deleteDeck(data: DeleteDeckData) {
        try {
            val deckReceived = findDeckRespository.find(data.toRequest())
            val deck = Deck(deckReceived.deckId, deckReceived.title, deckReceived.description, deckReceived.isFavorite, deckReceived.imageUri, deckReceived.cardIds)
            deleteDeckRepository.delete(deck.toDeleteDeckRequest())
            deleteCardRepository.delete(deck.toDeleteCardRequest())
        } catch (e: Throwable) {
            throw DeleteDeckError()
        }
    }

    inner class DeleteDeckError: Throwable()
}

private fun DeleteDeckData.toRequest() = FindDeckRequest(id)
private fun Deck.toDeleteDeckRequest() = DeleteDeckRequest(DeleteDeckRequest.Deck(
    deckId, title, description, isFavorite, imageUri, cardIds
))
private fun Deck.toDeleteCardRequest() = DeleteCardsRequest(cardIds)