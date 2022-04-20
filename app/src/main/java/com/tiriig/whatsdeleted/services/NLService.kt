package com.tiriig.whatsdeleted.services

import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.tiriig.whatsdeleted.utility.isValidTitle

class NLService : NotificationListenerService() {

    override fun onCreate() {
        super.onCreate()
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.tiriig.whatsdeleted")
        registerReceiver(NLServiceReceiver(), intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(NLServiceReceiver())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Return STICKY to prevent the automatic service termination
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn != null && sbn.packageName == "com.whatsapp") {
            sendMessage(sbn)
            handleDeletedMessage(sbn)
        }
    }

    private fun sendMessage(sbn: StatusBarNotification) {
        val intent = Intent("com.tiriig.whatsdeleted")

        val extras = sbn.notification.extras
        val title = extras.getString("android.title")
        val text = extras.getCharSequence("android.text").toString()

        intent.putExtra("time", sbn.notification.`when`)
        intent.putExtra("title", title)
        intent.putExtra("text", text)

        if (title != null && title.isValidTitle()) {
            sendBroadcast(intent)
        }
    }

    private fun handleDeletedMessage(sbn: StatusBarNotification) {
        val intent = Intent("com.tiriig.whatsdeleted")

        val extras = sbn.notification.extras
        val user = extras.getString("android.title")
        val message = extras.getCharSequence("android.text").toString()

        if (message == "This message was deleted"){
            intent.putExtra("isDeleted",true)
            intent.putExtra("title", user)
            sendBroadcast(intent)
        }
    }
}