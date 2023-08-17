package com.programou.shuffled.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.programou.shuffled.database.entities.DeckEntity


@Dao
interface DeckDao {
    object Constant {
        const val TABLE_NAME = "deck_table"
        const val DECK_ID = "deck_id"
        const val TRUE = 1
        const val IS_FAVORITE_PARAM = "is_favorite"
        const val DECK_ID_PARAM = "deck_id"
        const val CARD_IDS_PARAM = "card_ids"
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(deck: DeckEntity)

    @Query("SELECT * FROM ${Constant.TABLE_NAME} ORDER BY ${Constant.DECK_ID} ASC")
    suspend fun getAllDecks(): List<DeckEntity>

    @Query("SELECT * FROM ${Constant.TABLE_NAME} WHERE ${Constant.IS_FAVORITE_PARAM} = ${Constant.TRUE} ORDER BY ${Constant.DECK_ID} ASC")
    suspend fun getFavoriteDecks(): List<DeckEntity>

    @Query("SELECT * FROM ${Constant.TABLE_NAME} WHERE ${Constant.DECK_ID_PARAM} = :deckId")
    suspend fun findDeckBy(deckId: Long): DeckEntity

    @Update
    suspend fun updateDeck(deck: DeckEntity)

    @Query("UPDATE ${Constant.TABLE_NAME} SET ${Constant.IS_FAVORITE_PARAM} = :isFavorite WHERE ${Constant.DECK_ID_PARAM} = :deckId")
    suspend fun updateFavorite(deckId: Long, isFavorite: Boolean)

    @Query("UPDATE ${Constant.TABLE_NAME} SET ${Constant.CARD_IDS_PARAM} = :cardIds WHERE ${Constant.DECK_ID_PARAM} = :deckId")
    suspend fun updateCardId(deckId: Long, cardIds: List<Long>)

    @Delete
    suspend fun deleteDeck(deck: DeckEntity)
}