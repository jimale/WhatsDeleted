package com.tiriig.whatsdeleted.utility

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.material.chip.Chip
import com.tiriig.whatsdeleted.R


fun String.isValidTitle(): Boolean {
    return when (this) {
        "" -> false
        "WhatsApp" -> false
        "Telegram" -> false
        "DiscussBot" -> false
        "Checking for messages…" -> false
        "Signal" -> false
        "Calling…" -> false
        "Ringing…" -> false
        "Deleting messages…" -> false
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
        "This message was deleted." -> false
        else -> true
    }
}

fun String.isValidApp(): Boolean {
    return when (this) {
        "com.whatsapp" -> true
        "com.whatsapp.w4b" -> true
        "org.thoughtcrime.securesms" -> true
        "org.telegram.messenger" -> true
        else -> false
    }
}


fun String.name(): String {
    return when (this) {
        "com.whatsapp" -> "WhatsApp"
        "com.whatsapp.w4b" -> "WhatsApp business"
        "org.thoughtcrime.securesms" -> "Signal"
        "org.telegram.messenger" -> "Telegram"
        else -> "Undefined"
    }
}

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}


//fun String.fromJson(): List<Chat?>? {
//    val listType = object : TypeToken<List<Chat?>?>() {}.type
//    return Gson().fromJson<List<Chat?>>(this, listType)
//}

fun ImageView.loadImage(url: Int) {
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.chat_user)
        .transform(CircleCrop())
        .into(this)
}

fun Fragment.toast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun <T> Activity.startActivity(activity: Class<T>) {
    startActivity(Intent(this, activity))
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    finish()
}

fun Context.finishedIntro() {
    val editor = this.getSharedPreferences("DATA_STORE", Context.MODE_PRIVATE).edit()
    editor.putBoolean("finishedIntro", true)
    editor.apply()
}

fun Context.isFinishedIntro(): Boolean {
    val sharedPref = this.getSharedPreferences("DATA_STORE", Context.MODE_PRIVATE)
    return sharedPref.getBoolean("finishedIntro", false)
}

fun Chip.changeBackgroundColor(app: String?) {
    when (app) {
        "com.whatsapp" -> setChipBackgroundColorResource(R.color.whatsapp)
        "com.whatsapp.w4b" -> setChipBackgroundColorResource(R.color.whatsapp_business)
        "org.thoughtcrime.securesms" -> setChipBackgroundColorResource(R.color.signal)
        "org.telegram.messenger" -> setChipBackgroundColorResource(R.color.telegram)
        else -> this.hide()
    }
}