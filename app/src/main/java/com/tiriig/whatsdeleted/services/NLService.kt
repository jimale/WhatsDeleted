package com.tiriig.whatsdeleted.services

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.tiriig.whatsdeleted.data.model.Chat
import com.tiriig.whatsdeleted.data.repository.ChatRepository
import com.tiriig.whatsdeleted.utility.getRandomNum
import com.tiriig.whatsdeleted.utility.isValidApp
import com.tiriig.whatsdeleted.utility.isValidTitle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NLService : NotificationListenerService() {

    @Inject
    lateinit var repository: ChatRepository

    // This scope is tied to the service's lifecycle and uses a background thread.
    // A SupervisorJob ensures that if one child coroutine fails, the others are not cancelled.
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Return STICKY to ensure the service restarts if the system kills it.
        return START_STICKY
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        // Filter for valid notifications from the apps you support.
        if (sbn != null && sbn.packageName.isValidApp()) {
            processNotification(sbn)
        }
    }

    private fun processNotification(sbn: StatusBarNotification) {
        val extras = sbn.notification.extras
        val title = extras.getString("android.title") ?: return
        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        val time = sbn.notification.`when`
        val app = sbn.packageName

        // Ignore notifications with invalid titles or empty text (e.g., "Checking for new messages")
        if (!title.isValidTitle() || text.isEmpty()) return

        // Launch a coroutine on our background-threaded scope to do the heavy lifting.
        serviceScope.launch {
            saveNewMessage(title, text, time, app)
        }
    }

    private suspend fun saveNewMessage(title: String, text: String, time: Long, app: String) {
        val id = getRandomNum()

        if (title.contains(":")) {
            // Assumes "GroupName: SenderName" format
            var groupName = title.substringBefore(":")
            val senderName = title.substringAfter(": ").trim()

            // Clean up group name if it contains message counts (e.g., "My Group (2 messages)")
            if (groupName.contains("(")) {
                groupName = groupName.substringBefore("(").trim()
            }

            val groupChat = Chat(id, groupName, "$senderName: $text", time, app, isGroup = true)
            repository.saveMessage(groupChat)
        } else {
            // Standard direct message
            val chat = Chat(id, title, text, time, app)
            repository.saveMessage(chat)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancelling this is crucial to prevent memory leaks by stopping all ongoing coroutines
        serviceScope.cancel()
    }
}