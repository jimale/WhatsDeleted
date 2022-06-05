package com.tiriig.whatsdeleted.utility

import android.view.View
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


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

fun View.hide(){
    visibility = View.GONE
}

fun View.show(){
    visibility = View.VISIBLE
}


fun <T> String.fromJson(): List<T?>? {
    val listType = object : TypeToken<List<T?>?>() {}.type
    return Gson().fromJson<List<T?>>(this, listType)
}

@TypeConverter
fun <T> List<T>.toJson(): String? {
    val gson = Gson()
    return gson.toJson(this)
}