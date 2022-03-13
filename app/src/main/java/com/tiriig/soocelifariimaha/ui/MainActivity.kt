package com.tiriig.soocelifariimaha.ui

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.tiriig.soocelifariimaha.R
import com.tiriig.soocelifariimaha.databinding.ActivityMainBinding
import com.tiriig.soocelifariimaha.services.NLService
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var enableNotificationDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setUpMain()
    }

    private fun setUpMain() {
        // If the user did not turn the notification listener service on we prompt him to do so
        if (!isNotificationServiceEnabled()) {
            enableNotificationDialog = showNotificationDialog()
            enableNotificationDialog!!.show()
        }
        //Start the service
        val intent = Intent(applicationContext, NLService::class.java)
        startService(intent)

        //set up fragment Navigation
        val navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        setupActionBarWithNavController(navHost.navController)

        //Handling notification click
        val isNotificationClicked = intent.getBooleanExtra("notification", false)
        val user = intent.getStringExtra("user")
        if (isNotificationClicked) navHost.findNavController()
            .navigate(R.id.chatDetailFragment, bundleOf("user" to user))
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

    private fun showNotificationDialog(): AlertDialog {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Enable notification")
        alertDialogBuilder.setMessage("Please enable notification access to use the app")
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

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp()
    }
}