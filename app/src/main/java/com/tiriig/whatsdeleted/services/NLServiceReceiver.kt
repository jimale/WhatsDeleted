package com.tiriig.whatsdeleted.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.tiriig.whatsdeleted.data.model.Chat
import com.tiriig.whatsdeleted.data.repository.ChatRepository
import com.tiriig.whatsdeleted.utility.Notifications
import com.tiriig.whatsdeleted.utility.getRandomNum
import com.tiriig.whatsdeleted.utility.isValidTitle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NLServiceReceiver : BroadcastReceiver() {

    // ProcessLifecycleOwner provides lifecycle for the whole application process.
    private val applicationScope = ProcessLifecycleOwner.get().lifecycleScope

    @Inject
    lateinit var repository: ChatRepository

    @Inject
    lateinit var notifications: Notifications

    override fun onReceive(context: Context?, intent: Intent) {
        saveMessage(intent)
        notifyDeletedMessage(intent)
    }

    private fun saveMessage(intent: Intent) {
        //get data from the intent
        val title = intent.getStringExtra("title") ?: ""
        val text = intent.getStringExtra("text") ?: ""
        val time = intent.getLongExtra("time", 0)
        val app = intent.getStringExtra("app") ?: ""
        val id = getRandomNum()

        val chat = Chat(id, title, text, time, app)
        applicationScope.launch {
            //check if the message is from Group chat
            if (title.contains("messages") || title.contains(":")) {
                //Split the title because it contains the group name and user name
                var group = title.substringBefore(":")
                val user = title.substringAfter(": ")
                //Split the group if displays the number of unread messages
                if (group.endsWith("messages)")) group = group.split(" (")[0]

                val groupChat = Chat(id, group, "$user: $text", time, app, isGroup = true)
                if (text.isValidTitle()) repository.saveMessage(groupChat)
            } else {
                if (title.isValidTitle()) repository.saveMessage(chat)
            }
        }
    }

    private fun notifyDeletedMessage(intent: Intent) {
        val isDelete = intent.getBooleanExtra("isDeleted", false)
        val title = intent.getStringExtra("title") ?: ""
        val app = intent.getStringExtra("app") ?: ""

        if (isDelete) {
            applicationScope.launch {
                val lastMessage = repository.lastMessage(title)
                lastMessage?.let {
                    if (!lastMessage.isDeleted) {
                        //if the message is deleted notify the user and change delete state to deleted
                        notifications.notify(title, "$title deleted a message", "Click here to see",app)
                        repository.messageIsDeleted(lastMessage.id)
                    }
                }
            }
        }
    }
}