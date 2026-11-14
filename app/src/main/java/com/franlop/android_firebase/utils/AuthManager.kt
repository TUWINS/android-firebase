package com.franlop.android_firebase.utils

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await

sealed class AuthRes<out T> {
    data class Success<T>(val data: T) : AuthRes<T>()
    data class Error(val errorMessage: String) : AuthRes<Nothing>()
}

class AuthManager {
    private val auth: FirebaseAuth by lazy { Firebase.auth }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    suspend fun signInAnonymously(): AuthRes<FirebaseUser> {
        return try {
            val result = auth.signInAnonymously().await()
            AuthRes.Success(result.user ?: throw Exception("Error al iniciar sesión anónima"))
        } catch (e: Exception) {
            AuthRes.Error(e.message ?: "Error al iniciar sesión anónima")
        }
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthRes<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            AuthRes.Success(result.user ?: throw Exception("Error al iniciar sesión"))
        } catch (e: Exception) {
            AuthRes.Error(e.message ?: "Error al iniciar sesión")
        }
    }

    suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthRes<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            AuthRes.Success(result.user ?: throw Exception("Error al crear cuenta"))
        } catch (e: Exception) {
            AuthRes.Error(e.message ?: "Error al crear cuenta")
        }
    }

    suspend fun sendPasswordResetEmail(email: String): AuthRes<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            AuthRes.Success(Unit)
        } catch (e: Exception) {
            AuthRes.Error(e.message ?: "Error al enviar correo de recuperación")
        }
    }

    fun signOut() {
        auth.signOut()
    }
}
