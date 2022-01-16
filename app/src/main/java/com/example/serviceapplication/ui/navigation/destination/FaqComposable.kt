package com.example.serviceapplication.ui.navigation.destination

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.serviceapplication.ui.navigation.Navigator
import com.example.serviceapplication.ui.screen.faq.FaqScreen
import com.example.serviceapplication.viewModel.OidcViewModel

fun NavGraphBuilder.faqComposable(
    oidcViewModel: OidcViewModel,
    navigateToMain: () -> Unit
) {
    composable(
        route = Navigator.SCREEN_FAQ,
    ){ navBackStackEntry ->
        FaqScreen(
            oidcViewModel = oidcViewModel,
            navigateToMain = navigateToMain
        )
    }
}