package com.tiriig.soocelifariimaha.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Message(
    val user: String,
    val message: String,
    @PrimaryKey
    val time: String
)
