package com.tiriig.soocelifariimaha.ui

import android.content.*
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.tiriig.soocelifariimaha.data.model.Message
import com.tiriig.soocelifariimaha.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var notificationBroadcastReceiver: BroadcastReceiver
    private var enableNotificationDialog: AlertDialog? = null

    private val viewModel: MainViewModel by viewModels()

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

        fetchMessages()
    }

    private fun fetchMessages() {
        val adapter = MainAdapter()
        viewModel.getMessagesByUser("Sagal").observe(this) {
            adapter.submitList(it)
        }
        binding.recyclerView.adapter = adapter
    }

    private fun getNotification(){
        notificationBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                val user = intent.getStringExtra("user")?:""
                val text = intent.getStringExtra("text")?:""
                val time = intent.getLongExtra("time",0)
//                val id = intent.getIntExtra("id",0)
                //Toast.makeText(this@MainActivity, "$user || ID = $id", Toast.LENGTH_SHORT).show()

                //Save message to the database
                val message = Message(user,text,"09 may")
                viewModel.saveMessage(message)
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