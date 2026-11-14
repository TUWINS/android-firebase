package com.franlop.android_firebase.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.franlop.android_firebase.utils.AuthManager

@Composable
fun Home(
    authManager: AuthManager,
    navigateToLogin: () -> Unit = {}
) {
    val user = authManager.getCurrentUser()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Inicio", fontSize = 40.sp)
        Spacer(modifier = Modifier.height(20.dp))
        val userInfo = when {
            user == null -> "Sin sesión"
            user.isAnonymous -> "Sesión Anónima (Invitado)"
            else -> "Usuario: ${user.email}"
        }
        Text(text = userInfo, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(30.dp))
        Button(onClick = {
            authManager.signOut()
            navigateToLogin()
        }) {
            Text("Cerrar Sesión")
        }
    }
}
