package com.tiriig.soocelifariimaha.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tiriig.soocelifariimaha.data.model.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(data: Message)

    @Query("SELECT * from message")
    fun getMessages(): Flow<Message>

    @Query("SELECT * from message where user =:user")
    fun getMessagesByUser(user: String): Flow<Message>

    @Query("DELETE FROM Message")
    fun clear()
}
