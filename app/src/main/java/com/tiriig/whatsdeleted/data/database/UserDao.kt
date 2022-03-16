package com.tiriig.whatsdeleted.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tiriig.whatsdeleted.data.model.Chat
import com.tiriig.whatsdeleted.data.model.DeletedMessage

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(data: Chat)

    @Query("SELECT id,user, message, time, isDeleted from chat group by user order by time DESC")
    fun getChats(): LiveData<List<Chat>>

    @Query("SELECT * from chat where user =:user order by time DESC")
    fun getMessagesByUser(user: String): LiveData<List<Chat>>

    @Query("SELECT id,message,isDeleted from chat where user =:user order by time DESC LIMIT 1")
    fun getLastMessage(user: String): DeletedMessage?

    @Query("UPDATE chat set isDeleted =1 where id =:id")
    fun messageIsDeleted(id: String)

    @Query("DELETE FROM Chat")
    fun clear()
}
