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

        val user = intent.getStringExtra("user") ?: ""
        val text = intent.getStringExtra("text") ?: ""
        val time = intent.getLongExtra("time", 0)
        val id = getRandomNum()

        val isDelete = intent.getBooleanExtra("isDeleted", false)

        //Save message to the database
        val chat = Chat(id, user, text, time)
        applicationScope.launch {
            //check if the message from Group chat
            if (user.contains("messages") || user.contains(":")) {
                //Split the title because it contains the group name and user name
                var group = user.split(":")[0]
                val userName = user.split(":")[1]
                //Split the group if it displays the number of unread messages
                if (group.endsWith("messages)")) group = group.split(" (")[0]

                val groupChat = Chat(id, group, "$userName:$text", time)
                repository.saveMessage(groupChat)
            } else repository.saveMessage(chat)
        }

        //Notify user if message was deleted
        if (isDelete) {
            applicationScope.launch {
                val lastMessage = repository.lastMessage(user)
                lastMessage?.let {
                    if (!lastMessage.isDeleted){
                        //if the message is deleted notify the user and change delete state to deleted
                        notifications.notify(user, "$user was deleted a message", lastMessage.message)
                        repository.messageIsDeleted(lastMessage.id)
                    }
                }
            }
        }
    }
}