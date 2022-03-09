package com.tiriig.soocelifariimaha.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.tiriig.soocelifariimaha.data.model.Chat
import com.tiriig.soocelifariimaha.data.repository.ChatRepository
import com.tiriig.soocelifariimaha.ui.util.getRandomNum
import com.tiriig.soocelifariimaha.utility.Notifications
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

        val isDeleted = intent.getBooleanExtra("isDeleted", false)

        //Save message to the database
        val chat = Chat(id, user, text, time)
        applicationScope.launch {
            repository.saveMessage(chat)
        }

        //Notify user if message was deleted
        if (isDeleted) {
            applicationScope.launch {
                val message = repository.lastMessage(user)
                notifications.notify(user, "$user was deleted a message", message)
            }
        }
    }
}