package com.example

import android.app.Application
import com.example.skybound.data.AppDatabase
import com.example.skybound.notification.NotificationHelper

class SkyboundApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppDatabase.getDatabase(this)
        NotificationHelper.createNotificationChannels(this)
    }
}
