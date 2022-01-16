package com.example.serviceapplication.ui.navigation.destination

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.serviceapplication.ui.navigation.Navigator.Companion.SCREEN_SETUP
import com.example.serviceapplication.ui.screen.setup.SetupScreen
import com.example.serviceapplication.viewModel.OidcViewModel

@ExperimentalComposeUiApi
fun NavGraphBuilder.setupComposable(
    oidcViewModel: OidcViewModel,
    navigateToMain: () -> Unit,
    saveOidcServerUrlBtnClicked : () -> Unit
) {
    composable(
        route = SCREEN_SETUP,
    ){ navBackStackEntry ->
        SetupScreen(
            oidcViewModel = oidcViewModel,
            navigateToMain = navigateToMain,
            saveOidcServerUrlBtnClicked = saveOidcServerUrlBtnClicked
        )
    }
}
