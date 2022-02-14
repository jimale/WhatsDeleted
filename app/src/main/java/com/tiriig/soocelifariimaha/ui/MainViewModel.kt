package com.tiriig.soocelifariimaha.ui

import androidx.lifecycle.*

import com.tiriig.soocelifariimaha.data.model.Message
import com.tiriig.soocelifariimaha.data.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MessageRepository,
) : ViewModel() {

    fun saveMessage(message: Message) {
        viewModelScope.launch {
            repository.saveMessage(message)
        }
    }

    fun getMessages(): LiveData<List<Message>> = liveData {
        val response = repository.fetchMessages()
        emitSource(response)
    }

    fun getMessagesByUser(user: String): LiveData<List<Message>> = liveData {
        val response = repository.fetchMessagesByUser(user)
        emitSource(response)
    }
}