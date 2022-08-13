package com.tiriig.whatsdeleted.data.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity
data class Chat(
    @PrimaryKey
    val id: String,
    val user: String,
    val message: String,
    val time: Long,
    @ColumnInfo(defaultValue = "")
    val app: String,
    val isDeleted: Boolean = false,
    val isGroup: Boolean = false,
)
