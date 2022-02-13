package com.tiriig.soocelifariimaha.data.repository

import com.tiriig.soocelifariimaha.data.Database
import com.tiriig.soocelifariimaha.data.model.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepository @Inject constructor(
    private val database: Database
)  {

    suspend fun saveMessage(message: Message) {
        withContext(Dispatchers.IO) {
            database.userDao().save(message)
        }
    }

    suspend fun fetchMessages():Flow<Message>{
        return withContext(Dispatchers.IO){
            database.userDao().getMessages()
        }
    }

    suspend fun fetchMessagesByUser(user: String):Flow<Message>{
        return withContext(Dispatchers.IO){
            database.userDao().getMessagesByUser(user)
        }
    }
}