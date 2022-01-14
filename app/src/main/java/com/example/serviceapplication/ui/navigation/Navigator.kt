package com.example.serviceapplication.ui.navigation

import androidx.navigation.NavHostController

class Navigator(navController : NavHostController) {
    val toMain: () -> Unit = {
        navController.navigate( SCREEN_MAIN) {
            popUpTo( SCREEN_MAIN) { inclusive = true}
        }
    }

    val toSetup: () -> Unit = {
        navController.navigate( SCREEN_SETUP)
    }

    val toUserInfo: () -> Unit = {
        navController.navigate( SCREEN_USERINFO)
    }

    val toFaq: () -> Unit = {
        navController.navigate( SCREEN_FAQ)
    }

    companion object {
        const val SCREEN_MAIN       = "SCREEN_MAIN"
        const val SCREEN_SETUP      = "SCREEN_SETUP"
        const val SCREEN_USERINFO   = "SCREEN_USERINFO"
        const val SCREEN_FAQ        = "SCREEN_FAQ"
    }

}