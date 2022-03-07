package com.tiriig.soocelifariimaha.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.tiriig.soocelifariimaha.data.Database
import com.tiriig.soocelifariimaha.data.model.Chat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepository @Inject constructor(
    private val database: Database
)  {

    suspend fun saveMessage(chat: Chat) {
        withContext(Dispatchers.IO) {
            database.userDao().save(chat)
            Log.d("HHH","HHHHHHHHHHH $chat")
        }
    }

    suspend fun fetchMessages(): LiveData<List<Chat>> {
        return withContext(Dispatchers.IO){
            database.userDao().getMessages()
        }
    }

    suspend fun fetchMessagesByUser(user: String):LiveData<List<Chat>>{
        return withContext(Dispatchers.IO){
            database.userDao().getMessagesByUser(user)
        }
    }
}