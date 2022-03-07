package com.tiriig.soocelifariimaha.ui

import androidx.lifecycle.*

import com.tiriig.soocelifariimaha.data.model.Chat
import com.tiriig.soocelifariimaha.data.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MessageRepository,
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