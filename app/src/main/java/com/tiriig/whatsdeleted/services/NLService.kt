package com.tiriig.whatsdeleted.services

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import com.tiriig.whatsdeleted.R
import com.tiriig.whatsdeleted.data.model.Chat
import com.tiriig.whatsdeleted.data.repository.ChatRepository
import com.tiriig.whatsdeleted.utility.Notifications
import com.tiriig.whatsdeleted.utility.getRandomNum
import com.tiriig.whatsdeleted.utility.isDeletionNotice
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

    @Inject
    lateinit var notifications: Notifications

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
        val time = sbn.notification.`when`
        val app = sbn.packageName

        if (!title.isValidTitle()) return

        // Bundled notifications (phone locked) put all lines here; android.text has only the last one.
        val messagingStyleMessages = NotificationCompat.MessagingStyle
            .extractMessagingStyleFromNotification(sbn.notification)
            ?.messages
            ?.filter { it.text?.isNotEmpty() == true }

        serviceScope.launch {
            if (!messagingStyleMessages.isNullOrEmpty()) {
                messagingStyleMessages.forEach { message ->
                    saveNewMessage(title, message.text.toString(), message.timestamp, app)
                }
            } else {
                val text = extras.getCharSequence("android.text")?.toString() ?: ""
                if (text.isNotEmpty()) saveNewMessage(title, text, time, app)
            }
        }
    }

    private suspend fun saveNewMessage(title: String, text: String, time: Long, app: String) {
        if (title.contains(":")) {
            // Assumes "GroupName: SenderName" format
            var groupName = title.substringBefore(":")
            val senderName = title.substringAfter(": ").trim()

            // Clean up group name if it contains message counts (e.g., "My Group (2 messages)")
            if (groupName.contains("(")) {
                groupName = groupName.substringBefore("(").trim()
            }

            if (text.isDeletionNotice()) {
                flagLastMessageDeleted(groupName, app)
                return
            }

            val groupChat = Chat(getRandomNum(), groupName, "$senderName: $text", time, app, isGroup = true)
            repository.saveMessage(groupChat)
        } else {
            if (text.isDeletionNotice()) {
                flagLastMessageDeleted(title, app)
                return
            }

            // Standard direct message
            val chat = Chat(getRandomNum(), title, text, time, app)
            repository.saveMessage(chat)
        }
    }

    // The notification content is replaced rather than removed when a message is
    // deleted, so we just flag the chat's most recent message and let the user know.
    private suspend fun flagLastMessageDeleted(user: String, app: String) {
        val lastMessage = repository.lastMessage(user) ?: return
        if (lastMessage.isDeleted) return

        repository.messageIsDeleted(lastMessage.id)
        notifications.notify(
            user,
            getString(R.string.deleted_message_title),
            getString(R.string.deleted_message_body, user),
            app
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancelling this is crucial to prevent memory leaks by stopping all ongoing coroutines
        serviceScope.cancel()
    }
}