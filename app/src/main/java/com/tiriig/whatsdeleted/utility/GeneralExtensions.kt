package com.tiriig.whatsdeleted.utility


fun String.isValidTitle(): Boolean {
    return when (this) {
        "WhatsApp" -> false
        "Calling..." -> false
        "Ringing.." -> false
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