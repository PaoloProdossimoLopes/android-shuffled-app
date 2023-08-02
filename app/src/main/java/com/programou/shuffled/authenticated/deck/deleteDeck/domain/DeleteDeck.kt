package com.programou.shuffled.authenticated.deck.deleteDeck.domain

class DeleteDeck(
    private val findDeckRespository: FindDeckRepository,
    private val deleteDeckRepository: DeleteDeckRepository,
    private val deleteCardRepository: DeleteCardsRepository
): DeckDeletor {

    override suspend fun deleteDeck(data: DeleteDeckData) {
        try {
            val deck = findDeckRespository.find(data.toRequest())
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