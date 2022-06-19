package com.tiriig.whatsdeleted.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tiriig.whatsdeleted.data.model.Chat

@Database(
    entities = [Chat::class],
    version = 1,
    exportSchema = true
)

@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun userDao(): UserDao
}