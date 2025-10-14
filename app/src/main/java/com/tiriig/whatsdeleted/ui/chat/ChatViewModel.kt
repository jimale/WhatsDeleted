package com.tiriig.whatsdeleted.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.tiriig.whatsdeleted.data.model.Chat
import com.tiriig.whatsdeleted.data.model.ChatItem
import com.tiriig.whatsdeleted.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val getChatList: LiveData<List<Chat>> = liveData {
        val response = repository.fetchChats()
        emitSource(response)
    }

    val getChatByUser: LiveData<List<Chat>> = liveData {
        val user = savedStateHandle.get<String>("user") ?: ""
        val app = savedStateHandle.get<String>("app") ?: ""
        val response = repository.fetchMessagesByUser(user,app)
        emitSource(response)
    }
}