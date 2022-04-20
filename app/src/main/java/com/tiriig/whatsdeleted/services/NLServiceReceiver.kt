package com.tiriig.whatsdeleted.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.tiriig.whatsdeleted.data.model.Chat
import com.tiriig.whatsdeleted.data.repository.ChatRepository
import com.tiriig.whatsdeleted.utility.Notifications
import com.tiriig.whatsdeleted.utility.getRandomNum
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NLServiceReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: ChatRepository

    @Inject
    lateinit var notifications: Notifications

    override fun onReceive(context: Context?, intent: Intent) {
        // ProcessLifecycleOwner provides lifecycle for the whole application process.
        val applicationScope = ProcessLifecycleOwner.get().lifecycleScope

        val title = intent.getStringExtra("user") ?: ""
        val text = intent.getStringExtra("text") ?: ""
        val time = intent.getLongExtra("time", 0)
        val id = getRandomNum()

        val isDelete = intent.getBooleanExtra("isDeleted", false)

        //Save message to the database
        val chat = Chat(id, title, text, time)
        applicationScope.launch {
            //check if the message is from Group chat
            if (title.contains("messages") || title.contains(":")) {
                //Split the title because it contains the group name and user name
                var group = title.substringBefore(":")
                val user = title.substringAfter(": ")
                //Split the group if displays the number of unread messages
                if (group.endsWith("messages)")) group = group.split(" (")[0]

                val groupChat = Chat(id, group, "$user:$text", time)
                repository.saveMessage(groupChat)
            } else repository.saveMessage(chat)
        }

        //Notify user if message was deleted
        if (isDelete) {
            applicationScope.launch {
                val lastMessage = repository.lastMessage(title)
                lastMessage?.let {
                    if (!lastMessage.isDeleted){
                        //if the message is deleted notify the user and change delete state to deleted
                        notifications.notify(title, "$title deleted a message", "Click here to see")
                        repository.messageIsDeleted(lastMessage.id)
                    }
                }
            }
        }
    }
}