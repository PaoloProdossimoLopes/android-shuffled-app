package com.programou.shuffled.home.domain

interface AllDecksLoader {
    suspend fun load(): List<DeckViewData>
}