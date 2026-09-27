package com.tiriig.whatsdeleted.data.repository

import androidx.lifecycle.LiveData
import com.tiriig.whatsdeleted.data.database.Database
import com.tiriig.whatsdeleted.data.model.Chat
import com.tiriig.whatsdeleted.data.model.DeletedMessage
import com.tiriig.whatsdeleted.utility.isValidTitle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val database: Database
) {

    // WhatsApp/Telegram often re-post the same notification for one message within
    // milliseconds of each other, so the dedup check below needs to run atomically -
    // otherwise two overlapping saves both read "no duplicate yet" and both insert.
    private val saveMutex = Mutex()

    suspend fun saveMessage(chat: Chat) {
        withContext(Dispatchers.IO) {
            if (!chat.message.isValidTitle()) return@withContext

            saveMutex.withLock {
                //fetch last message and make comparison to avoid duplicates
                val lastMessage = database.userDao().getLastMessage(chat.user)
                val isDuplicate = lastMessage != null &&
                    (chat.message == lastMessage.message || chat.dateTime == lastMessage.dateTime)

                if (!isDuplicate) database.userDao().save(chat)
            }
        }
    }

    // Bundled (MessagingStyle) notifications re-post the whole conversation every time a
    // new message arrives, so the last-message check above would re-insert every earlier
    // line. Each line carries its own timestamp though, which is a reliable key per chat.
    suspend fun saveTimestampedMessage(chat: Chat) {
        withContext(Dispatchers.IO) {
            saveMutex.withLock {
                val existing = database.userDao().getMessageAt(chat.user, chat.app, chat.dateTime)
                if (existing == null) database.userDao().save(chat)
            }
        }
    }

    suspend fun messageAt(user: String, app: String, dateTime: Long): DeletedMessage? {
        return withContext(Dispatchers.IO) {
            database.userDao().getMessageAt(user, app, dateTime)
        }
    }

    suspend fun fetchChats(): LiveData<List<Chat>> {
        return withContext(Dispatchers.IO) {
            database.userDao().getChats()
        }
    }

    suspend fun fetchMessagesByUser(user: String,app: String): LiveData<List<Chat>> {
        return withContext(Dispatchers.IO) {
            database.userDao().getMessagesByUser(user,app)
        }
    }

    suspend fun lastMessage(user: String): DeletedMessage? {
        return withContext(Dispatchers.IO) {
            database.userDao().getLastMessage(user)
        }
    }

    suspend fun messageIsDeleted(id: String) {
        withContext(Dispatchers.IO) {
            database.userDao().messageIsDeleted(id)
        }
    }
}