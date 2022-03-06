package com.tiriig.soocelifariimaha.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Message(
    @PrimaryKey
    val id: String,
    val user: String,
    val message: String,
    val time: String
)
