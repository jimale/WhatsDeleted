package com.tiriig.whatsdeleted.services

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import com.tiriig.whatsdeleted.R
import com.tiriig.whatsdeleted.data.model.Chat
import com.tiriig.whatsdeleted.data.model.DeletedMessage
import com.tiriig.whatsdeleted.data.repository.ChatRepository
import com.tiriig.whatsdeleted.utility.Notifications
import com.tiriig.whatsdeleted.utility.getRandomNum
import com.tiriig.whatsdeleted.utility.groupName
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

        // Ignore notifications with invalid titles (e.g., "Checking for new messages")
        if (!title.isValidTitle()) return

        // Bundled notifications (several messages that arrived while the phone was locked)
        // carry every line in their MessagingStyle, android.text only has the last one.
        val style = NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(sbn.notification)
        if (style != null && style.messages.any { !it.text.isNullOrEmpty() }) {
            serviceScope.launch {
                saveBundledMessages(title, style, app)
            }
            return
        }

        // Ignore empty text
        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        if (text.isEmpty()) return

        // Launch a coroutine on our background-threaded scope to do the heavy lifting.
        serviceScope.launch {
            saveNewMessage(title, text, time, app)
        }
    }

    private suspend fun saveNewMessage(title: String, text: String, time: Long, app: String) {
        // Filtered on the raw text, before a group message gets its "Sender: " prefix
        if (!text.isValidTitle()) return

        if (title.contains(":")) {
            val groupName = title.groupName()
            val senderName = title.substringAfter(": ").trim()

            if (text.isDeletionNotice()) {
                flagDeleted(repository.lastMessage(groupName), groupName, app)
                return
            }

            val groupChat = Chat(getRandomNum(), groupName, "$senderName: $text", time, app, isGroup = true)
            repository.saveMessage(groupChat)
        } else {
            if (text.isDeletionNotice()) {
                flagDeleted(repository.lastMessage(title), title, app)
                return
            }

            // Standard direct message
            val chat = Chat(getRandomNum(), title, text, time, app)
            repository.saveMessage(chat)
        }
    }

    // The whole visible conversation is re-posted with every new message, so each line is
    // keyed by its own timestamp rather than compared against the chat's last message.
    private suspend fun saveBundledMessages(title: String, style: NotificationCompat.MessagingStyle, app: String) {
        val isGroup = style.isGroupConversation || title.contains(":")
        val user = when {
            !isGroup -> title
            title.contains(":") -> title.groupName()
            else -> (style.conversationTitle?.toString() ?: title).groupName()
        }
        val ownName = style.user.name?.toString()

        for (message in style.messages) {
            val text = message.text?.toString() ?: continue
            if (text.isEmpty() || !text.isValidTitle()) continue

            // No sender means the device owner wrote it (e.g. a reply from the notification)
            val sender = message.person?.name?.toString() ?: continue
            if (sender == ownName) continue

            if (text.isDeletionNotice()) {
                // The notice replaces the deleted line in place and keeps its timestamp,
                // so this finds the exact message instead of the chat's newest one.
                flagDeleted(repository.messageAt(user, app, message.timestamp), user, app)
                continue
            }

            val body = if (isGroup) "$sender: $text" else text
            repository.saveTimestampedMessage(Chat(getRandomNum(), user, body, message.timestamp, app, isGroup))
        }
    }

    // The notification content is replaced rather than removed when a message is
    // deleted, so we just flag the matching stored message and let the user know.
    private suspend fun flagDeleted(message: DeletedMessage?, user: String, app: String) {
        if (message == null || message.isDeleted) return

        repository.messageIsDeleted(message.id)
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