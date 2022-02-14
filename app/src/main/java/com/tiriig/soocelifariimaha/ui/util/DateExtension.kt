package com.tiriig.soocelifariimaha.ui.util

import android.text.format.DateFormat
import java.util.*

fun Long.getTime(): String {
// or you already have long value of date, use this instead of milliseconds variable.
    return DateFormat.format("MM/dd/yyyy/HH/mm/ss", Date(this)).toString()
}