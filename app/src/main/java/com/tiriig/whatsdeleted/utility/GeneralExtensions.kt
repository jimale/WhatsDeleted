package com.tiriig.whatsdeleted.utility

import android.view.View
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tiriig.whatsdeleted.data.model.Chat


fun String.isValidTitle(): Boolean {
    return when (this) {
        "" -> false
        "WhatsApp" -> false
        "Calling..." -> false
        "Ringing.." -> false
        "Deleting messages..." -> false
        "Ongoing voice call" -> false
        "WhatsApp Web" -> false
        "Finished backup" -> false
        "Backup in progress" -> false
        "Backup paused" -> false
        "Restoring media" -> false
        "Checking for new messages" -> false
        "WhatsApp Web is currently active" -> false
        "Tap for more info" -> false
        "Waiting for Wi-Fi" -> false
        "This message was deleted" -> false
        else -> true
    }
}

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}


fun String.fromJson(): List<Chat?>? {
    val listType = object : TypeToken<List<Chat?>?>() {}.type
    return Gson().fromJson<List<Chat?>>(this, listType)
}
