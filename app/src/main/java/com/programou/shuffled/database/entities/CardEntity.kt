package com.programou.shuffled.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "card_table")
data class CardEntity(
    @ColumnInfo(name = "question") val question: String,
    @ColumnInfo(name = "answer") val answer: String,
    @ColumnInfo(name = "studies_left") val studiesLeft: Int
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "card_id")
    var cardId: Long = 0
}