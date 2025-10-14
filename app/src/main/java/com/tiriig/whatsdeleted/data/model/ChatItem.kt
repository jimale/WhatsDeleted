package com.tiriig.whatsdeleted.data.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
sealed class ChatItem {
    data class DateItem(val date: String) : ChatItem()
    data class MessageItem(val chat: Chat) : ChatItem()
}