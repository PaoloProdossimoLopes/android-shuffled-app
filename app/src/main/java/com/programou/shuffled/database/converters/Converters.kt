package com.programou.shuffled.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun listToJsonString(value: List<Long>?): String = Gson().toJson(value)

    @TypeConverter
    fun jsonStringToList(value: String) = Gson().fromJson(value, Array<Long>::class.java).toList()
}