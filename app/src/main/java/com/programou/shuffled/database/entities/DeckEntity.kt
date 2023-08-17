package com.programou.shuffled.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deck_table")
data class DeckEntity(
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "is_favorite") var isFavorite: Boolean,
    @ColumnInfo(name = "image_uri") var imageUri: String,
    @ColumnInfo(name = "card_ids") var cardIds: List<Long>
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "deck_id")
    var deckId: Long = 0
}