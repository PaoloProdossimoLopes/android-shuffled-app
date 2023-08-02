package com.programou.shuffled.authenticated.deck.createFlashcard.domain

class CreateFlashcard(private val repository: CreateFlashcardRepository): FlashcardCreator {

    override suspend fun createFlashcard(data: CreateFlashcardData): Flashcard {
        val questionIsEmpty = data.flashcard.question.isEmpty()
        if (questionIsEmpty)
            throw FlashcardCreator.InvalidFlashcardError("question")

        val answerIsEmpty = data.flashcard.answer.isEmpty()
        if (answerIsEmpty)
            throw FlashcardCreator.InvalidFlashcardError("answer")

        try {
            return create(data)
        } catch (e: Exception) {
            throw FlashcardCreator.CreateFlashcardError()
        }
    }

    private suspend fun create(data: CreateFlashcardData): Flashcard {
        val studiesLeftMustHaveOnCreate = 0
        val request = data.toRequestWith(studiesLeftMustHaveOnCreate)
        val newFlashcardId = repository.createFlashcard(request)
        return Flashcard(newFlashcardId, data.flashcard.question, data.flashcard.answer, studiesLeftMustHaveOnCreate)
    }
}

private fun CreateFlashcardData.toRequestWith(studiesLeft: Int) = CreateFlashcardRequest(
    deckId,
    CreateFlashcardRequest.Flashcard(flashcard.question, flashcard.answer, studiesLeft)
)