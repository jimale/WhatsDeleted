package com.tiriig.soocelifariimaha.services

import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NotificationListener : NotificationListenerService() {

    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn != null && sbn.packageName == "com.whatsapp") {
            sendMessage(sbn)
        }
    }

    private fun sendMessage(sbn: StatusBarNotification) {
        val intent = Intent("com.tiriig.soocelifariimaha")

        var ticker = ""
        if (sbn.notification.tickerText != null) {
            ticker = sbn.notification.tickerText.toString()
        }
        val extras = sbn.notification.extras
        val title = extras.getString("android.title")
        val text = extras.getCharSequence("android.text").toString()


        intent.putExtra("time", sbn.postTime)
        intent.putExtra("user", title)
        intent.putExtra("text", text)
        intent.putExtra("id", sbn.key)

        sendBroadcast(intent)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        if (sbn != null && sbn.packageName == "com.whatsapp") {
            sendDeletedMessage(sbn)
        }
    }

    private fun sendDeletedMessage(sbn: StatusBarNotification) {
        val intent = Intent("com.tiriig.soocelifariimaha")

        val extras = sbn.notification.extras
        val title = extras.getString("android.title")
        val text = extras.getCharSequence("android.text").toString()

        intent.putExtra("time", sbn.postTime)
        intent.putExtra("user", title)
        intent.putExtra("text", text)
        sendBroadcast(intent)
    }
}