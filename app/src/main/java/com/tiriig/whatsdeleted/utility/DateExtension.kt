package com.tiriig.whatsdeleted.utility

import android.text.format.DateFormat
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun Long.formatDate(): String {
    val calendar = Calendar.getInstance()
    val currentDate = calendar.timeInMillis

    // Check if it's today
    val todayStart = calendar.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    // Check if it's yesterday
    val yesterdayStart = calendar.apply {
        add(Calendar.DATE, -1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    return when {
        this >= todayStart && this < currentDate -> "Today"
        this >= yesterdayStart && this < todayStart -> "Yesterday"
        else -> {
            // If before yesterday, format date as "dd MMM yyyy"
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            dateFormat.format(Date(this))
        }
    }
}


fun Long.formatTime(): String {
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return timeFormat.format(Date(this))
}

fun getRandomNum(): String {
    return BigInteger(130, Random()).toString(32).uppercase().substring(0, 10)
}

fun Long.getTime(): String {

    val sdf = SimpleDateFormat("MM-dd-yyyy-hh:mm aa", Locale.getDefault())
    val dateTime = sdf.format(this)
    val currentDate = sdf.parse(sdf.format(Date()))
    val previousDate = this

    //Check hours between current time and message time
    val diffInMillisec: Long = currentDate!!.time - previousDate
    val diffInHours: Long = TimeUnit.MILLISECONDS.toHours(diffInMillisec)

    return if (diffInHours < 12) dateTime.subSequence(11, 19).toString()
    else if (diffInHours > 48) dateTime.subSequence(0, 10).toString()
    else "Yesterday"
}