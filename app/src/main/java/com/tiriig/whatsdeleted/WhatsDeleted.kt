package com.tiriig.whatsdeleted

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WhatsDeleted: Application() {
    override fun onCreate() {
        super.onCreate()
        // Enable crashlytics if the app is in Production
        if (!BuildConfig.DEBUG) FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
    }
}