package com.tiriig.soocelifariimaha.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tiriig.soocelifariimaha.data.model.Chat
import com.tiriig.soocelifariimaha.data.repository.ChatRepository
import com.tiriig.soocelifariimaha.ui.util.getRandomNum
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NLServiceReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository : ChatRepository

    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context?, intent: Intent) {
        val user = intent.getStringExtra("user") ?: ""
        val text = intent.getStringExtra("text") ?: ""
        val time = intent.getLongExtra("time", 0)
        val id = getRandomNum()

        //Save message to the database
        val chat = Chat(id, user, text, time)
        GlobalScope.launch {
            repository.saveMessage(chat)
        }
    }
}