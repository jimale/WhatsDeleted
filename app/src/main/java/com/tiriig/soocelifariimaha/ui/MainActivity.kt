package com.tiriig.soocelifariimaha.ui

import android.content.*
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.tiriig.soocelifariimaha.databinding.ActivityMainBinding
import android.content.Intent

import android.content.BroadcastReceiver





class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var notificationBroadcastReceiver: BroadcastReceiver
    private var enableNotificationDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // If the user did not turn the notification listener service on we prompt him to do so
        if (!isNotificationServiceEnabled()) {
            enableNotificationDialog = buildNotificationServiceAlertDialog()
            enableNotificationDialog!!.show()
        }

        //get notifications
        getNotification()
    }

    private fun getNotification(){
        notificationBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                val receivedNotificationCode = intent.getStringExtra("text")

                binding.imageChangeExplanation.text = receivedNotificationCode
            }
        }

        // Finally we register a receiver to tell when a notification has been received
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.tiriig.soocelifariimaha")
        registerReceiver(notificationBroadcastReceiver, intentFilter)
    }

    private fun isNotificationServiceEnabled(): Boolean {
        val pkgName = packageName
        val flat: String = Settings.Secure.getString(
            contentResolver,
            "enabled_notification_listeners"
        )
        if (!TextUtils.isEmpty(flat)) {
            val names = flat.split(":").toTypedArray()
            for (i in names.indices) {
                val cn = ComponentName.unflattenFromString(names[i])
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.packageName)) {
                        return true
                    }
                }
            }
        }
        return false
    }


    private fun buildNotificationServiceAlertDialog(): AlertDialog {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("notification_listener_service")
        alertDialogBuilder.setMessage("notification_listener_service_explanation")
        alertDialogBuilder.setPositiveButton(
           "Yes"
        ) { dialog, id -> startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")) }
        alertDialogBuilder.setNegativeButton(
            "No"
        ) { dialog, id ->
            // If you choose to not enable the notification listener
            // the app. will not work as expected
        }
        return alertDialogBuilder.create()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(notificationBroadcastReceiver);
    }

}