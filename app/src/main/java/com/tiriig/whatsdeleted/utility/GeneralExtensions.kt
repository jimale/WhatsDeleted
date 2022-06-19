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
import com.tiriig.whatsdeleted.R


fun String.isValidTitle(): Boolean {
    return when (this) {
        "" -> false
        "WhatsApp" -> false
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
        else -> true
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

fun ImageView.loadImage(url: Int){
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.chat_user)
        .transform(CircleCrop())
        .into(this)
}

fun Fragment.toast(message: String){
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