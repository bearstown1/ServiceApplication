package com.example.serviceapplication.ui.navigation.destination

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.serviceapplication.viewModel.OidcViewModel
import com.example.serviceapplication.ui.navigation.Navigator.Companion.SCREEN_USERINFO
import com.example.serviceapplication.ui.screen.userinfo.UserInfoScreen

fun NavGraphBuilder.userInfoComposable(
    oidcViewModel: OidcViewModel,
    navigateToMain: () -> Unit
) {
    composable(
        route = SCREEN_USERINFO,
    ){ navBackStackEntry ->
        UserInfoScreen(
            oidcViewModel = oidcViewModel,
            navigateToMain = navigateToMain
        )
    }
}