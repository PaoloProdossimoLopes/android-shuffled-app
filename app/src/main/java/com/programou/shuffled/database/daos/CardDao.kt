package com.programou.shuffled.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.programou.shuffled.database.entities.CardEntity

@Dao
interface CardDao {
    object Constant {
        const val TABLE_NAME = "card_table"
        const val CARD_ID = "card_id"
        const val STUDIES_LEFT = "studies_left"
    }

    @Query("UPDATE ${Constant.TABLE_NAME} SET ${Constant.STUDIES_LEFT} = :studiesLeft WHERE ${Constant.CARD_ID} = :cardId")
    suspend fun updateStudiesLeft(cardId: Long, studiesLeft: Int)

    @Query("SELECT * FROM ${Constant.TABLE_NAME} WHERE ${Constant.CARD_ID} = :cardId")
    suspend fun findCardById(cardId: Long): CardEntity

    @Update
    suspend fun updateCard(card: CardEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCard(card: CardEntity): Long

    @Query("DELETE FROM ${Constant.TABLE_NAME} where ${Constant.CARD_ID} in (:ids)")
    suspend fun deleteCardWith(ids: List<Long>)
}