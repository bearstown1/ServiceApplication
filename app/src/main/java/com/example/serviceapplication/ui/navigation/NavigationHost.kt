package com.example.serviceapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.serviceapplication.viewModel.OidcViewModel
import com.example.serviceapplication.ui.navigation.Navigator.Companion.SCREEN_MAIN
import com.example.serviceapplication.ui.navigation.destination.mainComposable
import com.example.serviceapplication.ui.navigation.destination.setupComposable
import com.example.serviceapplication.ui.navigation.destination.userInfoComposable

@Composable
fun NavigationHost(
    navController: NavHostController,
    oidcViewModel: OidcViewModel,
    loginBtnClicked: () -> Unit,
    logoutBtnClicked: () -> Unit,
    saveUrlBtnClicked: () -> Unit
) {
    val navigator = remember (navController){
        Navigator( navController = navController)
    }

    NavHost(
        navController = navController,
        startDestination = SCREEN_MAIN
    ) {
        mainComposable(
            oidcViewModel = oidcViewModel,
            navigateToSetup = navigator.toSetup,
            navigateToUserInfo = navigator.toUserInfo,
            loginBtnClicked = loginBtnClicked,
            logoutBtnClicked = logoutBtnClicked,
        )

        setupComposable(
            oidcViewModel = oidcViewModel,
            navigateToMain = navigator.toMain,
            saveUrlBtnClicked = saveUrlBtnClicked
        )

        userInfoComposable(
            oidcViewModel = oidcViewModel,
            navigateToMain = navigator.toMain
        )
    }
}