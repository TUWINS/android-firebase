package com.franlop.android_firebase.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.franlop.android_firebase.screens.db.ContactsScreen
import com.franlop.android_firebase.utils.AuthManager
import com.franlop.android_firebase.utils.RealtimeManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    authManager: AuthManager,
    navigateToLogin: () -> Unit = {}
) {
    val context = LocalContext.current
    val realtimeManager = remember { RealtimeManager(context) }
    val user = authManager.getCurrentUser()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Contactos",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        val subtitle = when {
                            user == null -> "Sin sesión"
                            user.isAnonymous -> "Modo Invitado"
                            !user.email.isNullOrEmpty() -> user.email ?: ""
                            else -> "Usuario autenticado"
                        }
                        Text(
                            text = subtitle,
                            fontSize = 12.sp
                        )
                    }
                },
                actions = {
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
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            ContactsScreen(realtime = realtimeManager, authManager = authManager)
        }
    }
}
