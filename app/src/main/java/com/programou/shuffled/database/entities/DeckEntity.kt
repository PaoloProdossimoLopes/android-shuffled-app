package com.programou.shuffled.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deck_table")
data class DeckEntity(
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "is_favorite") val isFavorite: Boolean,
    @ColumnInfo(name = "image_uri") val imageUri: String,
    @ColumnInfo(name = "card_ids") val cardIds: List<Long>
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "deck_id")
    var deckId: Long = 0
}