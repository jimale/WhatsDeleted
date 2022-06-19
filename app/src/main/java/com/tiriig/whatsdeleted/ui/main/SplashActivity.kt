package com.tiriig.whatsdeleted.ui.main

import androidx.appcompat.app.AppCompatActivity
import com.tiriig.whatsdeleted.ui.intro.MyAppIntro
import com.tiriig.whatsdeleted.utility.isFinishedIntro
import com.tiriig.whatsdeleted.utility.startActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()
        val isFinished = isFinishedIntro()
        if (isFinished) startActivity(MainActivity::class.java)
        else startActivity(MyAppIntro::class.java)
    }
}