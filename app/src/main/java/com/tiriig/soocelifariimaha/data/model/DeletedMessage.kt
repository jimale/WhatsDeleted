package com.tiriig.soocelifariimaha.data.model

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
data class DeletedMessage(
    val id: String ="0",
    val message: String = "",
    val isDeleted: Boolean = false
)
