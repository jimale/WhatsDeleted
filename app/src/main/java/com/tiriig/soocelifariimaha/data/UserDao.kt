package com.tiriig.soocelifariimaha.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tiriig.soocelifariimaha.data.model.Chat

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(data: Chat)

    @Query("SELECT id,user, message, time from chat group by user order by time DESC")
    fun getChats(): LiveData<List<Chat>>

    @Query("SELECT * from chat where user =:user order by time DESC")
    fun getMessagesByUser(user: String): LiveData<List<Chat>>

    @Query("SELECT message from chat where user =:user order by time DESC LIMIT 1")
    fun getLastMessage(user: String): String?

    @Query("DELETE FROM Chat")
    fun clear()
}
