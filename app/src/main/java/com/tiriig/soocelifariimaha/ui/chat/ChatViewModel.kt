package com.tiriig.soocelifariimaha.ui.chat

import androidx.lifecycle.*

import com.tiriig.soocelifariimaha.data.model.Chat
import com.tiriig.soocelifariimaha.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository,
) : ViewModel() {

    fun saveMessage(chat: Chat) {
        viewModelScope.launch {
            repository.saveMessage(chat)
        }
    }

    fun getChat(): LiveData<List<Chat>> = liveData {
        val response = repository.fetchChats()
        emitSource(response)
    }

    fun getMessagesByUser(user: String): LiveData<List<Chat>> = liveData {
        val response = repository.fetchMessagesByUser(user)
        emitSource(response)
    }
}