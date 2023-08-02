package com.programou.shuffled.authenticated.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.programou.shuffled.authenticated.deck.DeckClienting
import com.programou.shuffled.authenticated.deck.DeckUpdateClienting
import com.programou.shuffled.authenticated.deck.DeckViewModel

class ResultViewModel: ViewModel() {
    fun configure(request: Request): Response {
        val deckStudiedTitle = request.viewData.deckTitle
        val congratulation = Response.Congratulation(
            Constant.CONGRATULATION_TITLE,
            Constant.CONGRATULATION_DESCRIPTION,
            deckStudiedTitle
        )

        val hardUsage = makeUsage(request.viewData.numberOfHard, request.viewData.totalOfCards, Constant.MARK_AS_HARD)
        val intermediateUsage = makeUsage(request.viewData.numberOfMid, request.viewData.totalOfCards, Constant.MARK_AS_INTERMEDIATE)
        val easyUsage = makeUsage(request.viewData.numberOfEasy, request.viewData.totalOfCards, Constant.MARK_AS_EASY)

        val usage = Response.Usage(hardUsage, intermediateUsage, easyUsage)
        return Response(congratulation, usage, Constant.COMPLETED_STUDY_MESSAGE)
    }

    private fun makeUsage(cards: Int, totalOfCards: Int, usageDescription: String): Response.Use {
        val percentage = calculatePercentageOf(cards, totalOfCards)
        val percentageFormatted = formatPercentage(percentage)
        return Response.Use(percentageFormatted, usageDescription)
    }

    private fun formatPercentage(value: Double): String {
        val numberFormatted = String.format(Constant.PERCENTAGE_FORMAT, value)
            .replace(Constant.DECIMAL_AMERICAN_SEPARATOR, Constant.DECIMAL_BRAZILIAN_SEPARATOR)
        return numberFormatted + Constant.PERCENTAGE_INDICATOR
    }

    fun calculatePercentageOf(cards: Int, forTotal: Int) = (cards * 100.0 / forTotal)

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