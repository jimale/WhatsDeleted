package com.tiriig.soocelifariimaha.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tiriig.soocelifariimaha.data.model.Message

@Database(
    entities = [Message::class],
    version = 1,
    exportSchema = true
)

abstract class Database : RoomDatabase() {
    abstract fun userDao(): UserDao
}