package com.mvi.todo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration

@HiltAndroidApp
class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // osmdroid configuration
        Configuration.getInstance().userAgentValue = packageName
    }
}