package com.example.walkin.app

import android.app.Application
import android.content.Context
import com.example.walkin.utils.PreferenceUtils

class WalkinApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        PreferenceUtils.init(applicationContext)
        appContext = applicationContext
    }

    companion object {
        lateinit var appContext: Context
    }
}