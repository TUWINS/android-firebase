package com.franlop.android_firebase.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.franlop.android_firebase.ui.theme.Purple40
import com.franlop.android_firebase.utils.AuthManager
import com.franlop.android_firebase.utils.AuthRes
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    authManager: AuthManager,
    navigateToHome: () -> Unit,
    navigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val email = remember { mutableStateOf(TextFieldValue()) }
    val password = remember { mutableStateOf(TextFieldValue()) }
    val errorMessage = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Crear Cuenta",
            style = TextStyle(fontSize = 36.sp, color = Purple40)
        )

        Spacer(modifier = Modifier.height(40.dp))
        TextField(
            label = { Text(text = "Correo electrónico") },
            value = email.value,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            onValueChange = { email.value = it }
        )

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            label = { Text(text = "Contraseña") },
            value = password.value,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onValueChange = { password.value = it }
        )

        if (errorMessage.value.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = errorMessage.value,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            Button(
                onClick = {
                    val emailText = email.value.text.trim()
                    val passwordText = password.value.text.trim()
                    if (emailText.isNotEmpty() && passwordText.isNotEmpty()) {
                        scope.launch {
                            when (val res = authManager.createUserWithEmailAndPassword(emailText, passwordText)) {
                                is AuthRes.Success -> {
                                    Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                    navigateToHome()
                                }
                                is AuthRes.Error -> {
                                    errorMessage.value = res.errorMessage
                                }
                            }
                        }
                    } else {
                        errorMessage.value = "Por favor, completa todos los campos"
                    }
                },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Registrarse")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = { navigateToLogin() }
        ) {
            Text(text = "Ya tengo cuenta, Iniciar Sesión")
        }
    }
}
