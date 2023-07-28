package com.programou.shuffled.authenticated.result

import androidx.lifecycle.ViewModel

class ResultViewModel: ViewModel() {
    fun configure(request: Request): Response {
        val deckStudiedTitle = request.viewData.deckTitle
        val congratulation = Response.Congratulation(
            Constant.CONGRATULATION_TITLE,
            Constant.CONGRATULATION_DESCRIPTION,
            deckStudiedTitle
        )

        val hardUsage = makeHardUsage(request)
        val intermediateUsage = makeIntermediateUsage(request)
        val easyUsage = makeEasyUsage(request)

        val usage = Response.Usage(hardUsage, intermediateUsage, easyUsage)
        return Response(congratulation, usage, Constant.COMPLETED_STUDY_MESSAGE)
    }

    private fun makeEasyUsage(request: Request): Response.Use {
        val easyPercentageValue = getPercentageOfEasy(request)
        val easyPercentage = formatPercentage(easyPercentageValue)
        return Response.Use(easyPercentage, Constant.MARK_AS_EASY)
    }

    private fun makeIntermediateUsage(request: Request): Response.Use {
        val intermediatePercentageValue = getPercentageOfIntermediate(request)
        val intermediatePercentage = formatPercentage(intermediatePercentageValue)
        return Response.Use(intermediatePercentage, Constant.MARK_AS_INTERMEDIATE)
    }

    private fun makeHardUsage(request: Request): Response.Use {
        val hardPercentageValue = getPercentageOfHard(request)
        val hardPercentage = formatPercentage(hardPercentageValue)
        return Response.Use(hardPercentage, Constant.MARK_AS_HARD)
    }

    private fun formatPercentage(value: Double): String {
        val numberFormatted = String.format(Constant.PERCENTAGE_FORMAT, value)
            .replace(Constant.DECIMAL_AMERICAN_SEPARATOR, Constant.DECIMAL_BRAZILIAN_SEPARATOR)
        return numberFormatted + Constant.PERCENTAGE_INDICATOR
    }

    fun getPercentageOfHard(request: Request): Double {
        val numberOfCardsSelectedAsHard = request.viewData.numberOfHard
        val totalOfCardsInDeck = request.viewData.totalOfCards
        return calculatePercentageOf(numberOfCardsSelectedAsHard, totalOfCardsInDeck)
    }

    fun getPercentageOfIntermediate(request: Request): Double {
        val numberOfCardsSelectedAsIntermediate = request.viewData.numberOfMid
        val totalOfCardsInDeck = request.viewData.totalOfCards
        return calculatePercentageOf(numberOfCardsSelectedAsIntermediate, totalOfCardsInDeck)
    }

    fun getPercentageOfEasy(request: Request): Double {
        val numberOfCardsSelectedAsEasy = request.viewData.numberOfEasy
        val totalOfCardsInDeck = request.viewData.totalOfCards
        return calculatePercentageOf(numberOfCardsSelectedAsEasy, totalOfCardsInDeck)
    }

    private fun calculatePercentageOf(cards: Int, forTotal: Int) = (cards * 100.0 / forTotal)

    private object Constant {
        const val PERCENTAGE_FORMAT = "%.2f"
        const val PERCENTAGE_INDICATOR = "%"
        const val DECIMAL_AMERICAN_SEPARATOR = "."
        const val DECIMAL_BRAZILIAN_SEPARATOR = ","
        const val MARK_AS_HARD = "marcado como difícil"
        const val MARK_AS_INTERMEDIATE = "marcado como médio"
        const val MARK_AS_EASY = "marcado como facil"
        const val COMPLETED_STUDY_MESSAGE = "Sua sessao de estudo chgou ao fim"
        const val CONGRATULATION_TITLE = "Bom trabalho!"
        const val CONGRATULATION_DESCRIPTION = "Você teve seus conecimentos melhorados em:"
    }

    data class Request(val viewData: ResultViewData)
    data class Response(
        val congratulation: Congratulation,
        val usage: Usage,
        val message: String
    ) {
        data class Congratulation(val title: String, val description: String, val name: String)
        data class Usage(val hard: Use, val intermediate: Use, val easy: Use)
        data class Use(val percentage: String, val description: String)
    }
}