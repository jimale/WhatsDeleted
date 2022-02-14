package com.tiriig.soocelifariimaha.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
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
            Log.d("HHH","HHHHHHHHHHH $message")
        }
    }

    suspend fun fetchMessages(): LiveData<List<Message>> {
        return withContext(Dispatchers.IO){
            database.userDao().getMessages()
        }
    }

    suspend fun fetchMessagesByUser(user: String):LiveData<List<Message>>{
        return withContext(Dispatchers.IO){
            database.userDao().getMessagesByUser(user)
        }
    }
}