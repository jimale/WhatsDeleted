package com.tiriig.whatsdeleted.utility

import android.text.format.DateFormat
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.*

fun Long.getTimeAndDate(): String {
    return DateFormat.format("MM-dd-yyyy-HH:mm:ss", Date(this)).toString()
}

fun getRandomNum(): String {
    return BigInteger(130, Random()).toString(32).uppercase().substring(0, 10)
}

fun Long.getTime(): String {

    val sdf = SimpleDateFormat("MM-dd-yyyy-hh:mm aa", Locale.getDefault())
    val dateTime = sdf.format(this)
    val currentDate = sdf.parse(sdf.format(Date()))
    val previousDate = this

    //Check days between current time and message time
    return when ((((currentDate!!.time - previousDate) / (1000 * 60 * 60 * 24)) + 1)) {
        1L -> {
            dateTime.subSequence(11,19).toString()
        }
        2L -> {
            "Yesterday"
        }
        else -> {
            dateTime.subSequence(0,10).toString()
        }
    }

}