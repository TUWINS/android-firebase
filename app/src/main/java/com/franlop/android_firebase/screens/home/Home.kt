package com.franlop.android_firebase.screens.home

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.franlop.android_firebase.R
import com.franlop.android_firebase.screens.db.ContactsScreen
import com.franlop.android_firebase.screens.db.NotesScreen
import com.franlop.android_firebase.utils.AuthManager
import com.franlop.android_firebase.utils.FirestoreManager
import com.franlop.android_firebase.utils.RealtimeManager
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.remoteConfig

private const val WELCOME_MESSAGE_KEY = "welcome_message"
private const val IS_BUTTON_VISIBLE_KEY = "is_button_visible"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    authManager: AuthManager,
    navigateToLogin: () -> Unit = {}
) {
    val context = LocalContext.current
    val realtimeManager = remember { RealtimeManager(context) }
    val firestoreManager = remember { FirestoreManager(context) }
    val user = authManager.getCurrentUser()
    var selectedTab by remember { mutableIntStateOf(0) }

    var welcomeMessage by remember { mutableStateOf("Bienvenidx") }
    var isWarningButtonVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                welcomeMessage = remoteConfig.getString(WELCOME_MESSAGE_KEY)
                isWarningButtonVisible = remoteConfig.getBoolean(IS_BUTTON_VISIBLE_KEY)
            }
        }

        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                Log.d("RemoteConfig", "Claves actualizadas: ${configUpdate.updatedKeys}")
                if (configUpdate.updatedKeys.contains(WELCOME_MESSAGE_KEY) ||
                    configUpdate.updatedKeys.contains(IS_BUTTON_VISIBLE_KEY)
                ) {
                    remoteConfig.activate().addOnCompleteListener {
                        welcomeMessage = remoteConfig.getString(WELCOME_MESSAGE_KEY)
                        isWarningButtonVisible = remoteConfig.getBoolean(IS_BUTTON_VISIBLE_KEY)
                    }
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.e("RemoteConfig", "Error al actualizar config", error)
            }
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        val headerText = if (selectedTab == 0) "Contactos (Realtime DB)" else "Notas (Firestore)"
                        Text(
                            text = headerText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        val subtitle = when {
                            user == null -> "Sin sesión"
                            user.isAnonymous -> "$welcomeMessage (Invitado)"
                            !user.email.isNullOrEmpty() -> "$welcomeMessage - ${user.email}"
                            else -> welcomeMessage
                        }
                        Text(
                            text = subtitle,
                            fontSize = 12.sp
                        )
                    }
                },
                actions = {
                    if (isWarningButtonVisible) {
                        IconButton(onClick = {
                            val crashlytics = com.google.firebase.crashlytics.FirebaseCrashlytics.getInstance()
                            crashlytics.setCustomKey("usuarioEmail", user?.email ?: "anonimo")
                            crashlytics.log("Error forzado desde Home")
                            throw RuntimeException("Error forzado de prueba en HomeScreen para Crashlytics")
                        }) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Forzar Error"
                            )
                        }
                    }
                    IconButton(onClick = {
                        authManager.signOut()
                        navigateToLogin()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar Sesión"
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Contactos") },
                    label = { Text("Contactos") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Notas") },
                    label = { Text("Notas") }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> ContactsScreen(realtime = realtimeManager, authManager = authManager)
                1 -> NotesScreen(firestore = firestoreManager)
            }
        }
    }
}
