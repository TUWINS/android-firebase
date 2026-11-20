package com.franlop.android_firebase

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class AndroidFirebaseApp : Application() {
    companion object {
        const val FCM_CHANNEL_ID = "fcm_default_channel"
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val fcmChannel = NotificationChannel(
                FCM_CHANNEL_ID,
                "Notificaciones Firebase",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para notificaciones de prueba de FCM"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(fcmChannel)
        }
    }
}
