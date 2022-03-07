package com.tiriig.soocelifariimaha.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Chat(
    @PrimaryKey
    val id: String,
    val user: String,
    val message: String,
    val time: Long
)
