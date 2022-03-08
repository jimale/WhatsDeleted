package com.tiriig.soocelifariimaha.ui

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.tiriig.soocelifariimaha.databinding.ActivityMainBinding
import com.tiriig.soocelifariimaha.services.NLService
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
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

        val intent = Intent(applicationContext, NLService::class.java)
        startService(intent)

        fetchMessages()
    }

    private fun fetchMessages() {
        val adapter = MainAdapter()
        viewModel.getChat().observe(this) {
            adapter.submitList(it)
            binding.recyclerView.adapter = adapter
        }
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
        ) { _, _ -> startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")) }
        alertDialogBuilder.setNegativeButton(
            "No"
        ) { _, _ ->
            finish()
        }
        return alertDialogBuilder.create()
    }
}