package com.programou.shuffled.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.programou.shuffled.database.converters.Converters
import com.programou.shuffled.database.daos.CardDao
import com.programou.shuffled.database.daos.DeckDao
import com.programou.shuffled.database.entities.CardEntity
import com.programou.shuffled.database.entities.DeckEntity

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