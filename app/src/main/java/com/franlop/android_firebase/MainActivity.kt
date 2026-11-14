package com.franlop.android_firebase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.franlop.android_firebase.navigation.Navigation
import com.franlop.android_firebase.ui.theme.AndroidFirebaseTheme
import com.google.firebase.analytics.FirebaseAnalytics

class MainActivity : ComponentActivity() {
    private lateinit var analytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analytics = FirebaseAnalytics.getInstance(this)
        setContent {
            AndroidFirebaseTheme {
                Navigation(analytics = analytics)
            }
        }
    }
}