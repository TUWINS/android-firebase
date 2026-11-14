package com.franlop.android_firebase.navigation

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.franlop.android_firebase.screens.Screen
import com.franlop.android_firebase.screens.auth.ForgotPasswordScreen
import com.franlop.android_firebase.screens.auth.SignUpScreen
import com.franlop.android_firebase.screens.home.Home
import com.franlop.android_firebase.screens.login.Login
import com.franlop.android_firebase.utils.AuthManager
import com.google.firebase.analytics.FirebaseAnalytics

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    analytics: FirebaseAnalytics? = null
) {
    val authManager = remember { AuthManager() }
    val currentUser = authManager.getCurrentUser()
    val startDestination = if (currentUser != null) Routes.Home.route else Routes.Login.route

    Screen {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable(Routes.Login.route) {
                analytics?.let { TrackScreen(name = Routes.Login.route, analytics = it) }
                Login(
                    authManager = authManager,
                    navigateToHome = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    },
                    navigateToSignUp = {
                        navController.navigate(Routes.SignUp.route)
                    },
                    navigateToForgotPassword = {
                        navController.navigate(Routes.ForgotPassword.route)
                    }
                )
            }
            composable(Routes.SignUp.route) {
                analytics?.let { TrackScreen(name = Routes.SignUp.route, analytics = it) }
                SignUpScreen(
                    authManager = authManager,
                    navigateToHome = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.SignUp.route) { inclusive = true }
                        }
                    },
                    navigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Routes.ForgotPassword.route) {
                analytics?.let { TrackScreen(name = Routes.ForgotPassword.route, analytics = it) }
                ForgotPasswordScreen(
                    authManager = authManager,
                    navigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Routes.Home.route) {
                analytics?.let { TrackScreen(name = Routes.Home.route, analytics = it) }
                Home(
                    authManager = authManager,
                    navigateToLogin = {
                        navController.navigate(Routes.Login.route) {
                            popUpTo(Routes.Home.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun TrackScreen(name: String, analytics: FirebaseAnalytics) {
    DisposableEffect(name) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, name)
        }
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        onDispose { }
    }
}
