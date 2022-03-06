package com.tiriig.soocelifariimaha.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tiriig.soocelifariimaha.data.model.Message

class Converters {

    @TypeConverter
    fun fromMessage(data: String?): List<Message?>? {
        val listType = object : TypeToken<List<Message?>?>() {}.type
        return Gson().fromJson<List<Message?>>(data, listType)
    }

    @TypeConverter
    fun toMessage(list: List<Message?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }
}
