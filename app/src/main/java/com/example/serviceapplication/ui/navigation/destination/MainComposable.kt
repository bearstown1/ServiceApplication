package com.example.serviceapplication.ui.navigation.destination

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.serviceapplication.ui.navigation.Navigator.Companion.SCREEN_MAIN
import com.example.serviceapplication.ui.screen.main.MainScreen
import com.example.serviceapplication.viewModel.OidcViewModel

fun NavGraphBuilder.mainComposable(
    oidcViewModel: OidcViewModel,
    navigateToSetup: () -> Unit,
    navigateToUserInfo: () -> Unit,
    navigateToFaq: () -> Unit,
    loginBtnClicked: () -> Unit,
    logoutBtnClicked: () -> Unit,
) {
    composable(
        route = SCREEN_MAIN,
    ){ navBackStackEntry ->

        MainScreen(
            oidcViewModel = oidcViewModel,
            navigateToSetup = navigateToSetup,
            navigateToUserInfo = navigateToUserInfo,
            navigateToFaq = navigateToFaq,
            loginBtnClicked = loginBtnClicked,
            logoutBtnClicked = logoutBtnClicked,
        )
    }
}