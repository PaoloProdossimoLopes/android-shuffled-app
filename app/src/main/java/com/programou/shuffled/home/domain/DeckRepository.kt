package com.programou.shuffled.home.domain

interface DeckRepository {
    suspend fun findAll(): List<Deck>
}