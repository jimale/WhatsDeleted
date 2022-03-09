package com.tiriig.soocelifariimaha.services

import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NLService : NotificationListenerService() {

    override fun onCreate() {
        super.onCreate()
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.tiriig.soocelifariimaha")
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
        }
    }

    private fun sendMessage(sbn: StatusBarNotification) {
        val intent = Intent("com.tiriig.soocelifariimaha")

        val extras = sbn.notification.extras
        val title = extras.getString("android.title")
        val text = extras.getCharSequence("android.text").toString()

        intent.putExtra("time", sbn.notification.`when`)
        intent.putExtra("user", title)
        intent.putExtra("text", text)

        //handling deleted message
        handleDeletedMessage(sbn)

        sendBroadcast(intent)
    }

    private fun handleDeletedMessage(sbn: StatusBarNotification) {
        val intent = Intent("com.tiriig.soocelifariimaha")

        val extras = sbn.notification.extras
        val user = extras.getString("android.title")
        val message = extras.getCharSequence("android.text").toString()

        if (message == "This message was deleted"){
            intent.putExtra("isDeleted",true)
            intent.putExtra("user", user)
            sendBroadcast(intent)
        }
    }
}