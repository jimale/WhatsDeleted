package com.tiriig.soocelifariimaha.services

import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification


class NotificationListener: NotificationListenerService() {

    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        val intent = Intent("com.tiriig.soocelifariimaha")

        var ticker = ""
        if (sbn!!.notification.tickerText != null) {
            ticker = sbn.notification.tickerText.toString()
        }
        val extras = sbn.notification.extras
        val title = extras.getString("android.title")
        val text = extras.getCharSequence("android.text").toString()


        intent.putExtra("ticker", ticker)
        intent.putExtra("title", title)
        intent.putExtra("text", title+"\n"+text)

        sendBroadcast(intent)

    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        val intent = Intent("com.tiriig.soocelifariimaha")

        val extras = sbn!!.notification.extras
        val title = extras.getString("android.title")
        val text = extras.getCharSequence("android.text").toString()

        intent.putExtra("title", title)
        intent.putExtra("text", title+"\n"+text)
        sendBroadcast(intent)
    }
}