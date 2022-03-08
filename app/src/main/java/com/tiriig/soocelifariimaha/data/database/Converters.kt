package com.tiriig.soocelifariimaha.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tiriig.soocelifariimaha.data.model.Chat

class Converters {

    @TypeConverter
    fun fromMessage(data: String?): List<Chat?>? {
        val listType = object : TypeToken<List<Chat?>?>() {}.type
        return Gson().fromJson<List<Chat?>>(data, listType)
    }

    @TypeConverter
    fun toMessage(list: List<Chat?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }
}
