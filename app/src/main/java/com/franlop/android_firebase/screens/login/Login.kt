package com.franlop.android_firebase.screens.login

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.franlop.android_firebase.ui.theme.Purple40
import com.franlop.android_firebase.utils.AuthManager
import com.franlop.android_firebase.utils.AuthRes
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

@Composable
fun Login(
    authManager: AuthManager,
    navigateToHome: () -> Unit,
    navigateToSignUp: () -> Unit = {},
    navigateToForgotPassword: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val email = remember { mutableStateOf(TextFieldValue()) }
    val password = remember { mutableStateOf(TextFieldValue()) }
    val errorMessage = remember { mutableStateOf("") }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (val accountRes = authManager.handleSignInResult(GoogleSignIn.getSignedInAccountFromIntent(result.data))) {
            is AuthRes.Success -> {
                val credential = GoogleAuthProvider.getCredential(accountRes.data.idToken, null)
                scope.launch {
                    when (val res = authManager.signInWithGoogleCredential(credential)) {
                        is AuthRes.Success -> {
                            Toast.makeText(context, "Bienvenido", Toast.LENGTH_SHORT).show()
                            navigateToHome()
                        }
                        is AuthRes.Error -> {
                            errorMessage.value = res.errorMessage
                        }
                    }
                }
            }
            is AuthRes.Error -> {
                errorMessage.value = accountRes.errorMessage
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ClickableText(
            text = AnnotatedString("¿No tienes una cuenta? Regístrate"),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(50.dp),
            onClick = { navigateToSignUp() },
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Default,
                textDecoration = TextDecoration.Underline,
                color = Purple40
            )
        )
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Firebase Login",
            style = TextStyle(fontSize = 40.sp, color = Purple40)
        )

        Spacer(modifier = Modifier.height(30.dp))
        TextField(
            label = { Text(text = "Correo electrónico") },
            value = email.value,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            onValueChange = { email.value = it })

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            label = { Text(text = "Contraseña") },
            value = password.value,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onValueChange = { password.value = it })

        if (errorMessage.value.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = errorMessage.value,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            Button(
                onClick = {
                    val emailText = email.value.text.trim()
                    val passwordText = password.value.text.trim()
                    if (emailText.isNotEmpty() && passwordText.isNotEmpty()) {
                        scope.launch {
                            when (val res = authManager.signInWithEmailAndPassword(emailText, passwordText)) {
                                is AuthRes.Success -> {
                                    navigateToHome()
                                }
                                is AuthRes.Error -> {
                                    errorMessage.value = res.errorMessage
                                }
                            }
                        }
                    } else {
                        errorMessage.value = "Por favor, ingresa correo y contraseña"
                    }
                },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Iniciar Sesión")
            }
        }

        Spacer(modifier = Modifier.height(15.dp))
        OutlinedButton(
            onClick = {
                scope.launch {
                    when (val res = authManager.signInAnonymously()) {
                        is AuthRes.Success -> {
                            Toast.makeText(context, "Sesión de invitado iniciada", Toast.LENGTH_SHORT).show()
                            navigateToHome()
                        }
                        is AuthRes.Error -> {
                            errorMessage.value = res.errorMessage
                        }
                    }
                }
            },
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(45.dp)
        ) {
            Text(text = "Ingresar como Invitado")
        }

        Spacer(modifier = Modifier.height(15.dp))
        OutlinedButton(
            onClick = {
                authManager.signInWithGoogle(googleSignInLauncher)
            },
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(45.dp)
        ) {
            Text(text = "Continuar con Google")
        }

        Spacer(modifier = Modifier.height(30.dp))
        ClickableText(
            text = AnnotatedString("¿Olvidaste tu contraseña?"),
            onClick = { navigateToForgotPassword() },
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Default,
                textDecoration = TextDecoration.Underline,
                color = Purple40
            )
        )
    }
}
