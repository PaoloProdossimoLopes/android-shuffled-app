package com.programou.shuffled.database

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Update
import com.google.gson.Gson
import com.programou.shuffled.authenticated.createDeck.CreateDeckModel
import com.programou.shuffled.authenticated.createDeck.CreateDeckRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

class Converters {

    @TypeConverter
    fun listToJsonString(value: List<Long>?): String = Gson().toJson(value)

    @TypeConverter
    fun jsonStringToList(value: String) = Gson().fromJson(value, Array<Long>::class.java).toList()
}

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


//    @Query("UPDATE ${Constant.TABLE_NAME} SET cards = :cards WHERE ${Constant.DECK_ID_PARAM} = :deckId")
//    suspend fun updateCard(deckId: Long, cards: List<CardEntity>)

    @Delete
    suspend fun deleteDeck(deck: DeckEntity)
}

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

@Database(entities = [DeckEntity::class, CardEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ShuffledDatabase: RoomDatabase() {
    abstract fun deckDao(): DeckDao
    abstract fun cardDao(): CardDao

    companion object {
        private var shared: ShuffledDatabase? = null

        fun getDatabase(context: Context): ShuffledDatabase {
            return shared ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ShuffledDatabase::class.java,
                    "shuffle_database"
                ).build()

                shared = instance

                instance
            }
        }
    }
}

class CreateDeckRepositoryAdapter(private val context: Context): CreateDeckRepository {
    private val db: ShuffledDatabase by lazy {
        ShuffledDatabase.getDatabase(context)
    }

    override fun saveDeck(deck: CreateDeckModel, onComplete: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val cardEntities = deck.cards.map { it.id.toLong() }
            val deckEntity = DeckEntity(deck.title, deck.description, deck.isFavorited, deck.imageUri.toString(), cardEntities)
            db.deckDao().insert(deckEntity)

            onComplete(true)
        }
    }
}