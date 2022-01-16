package com.example.serviceapplication.ui.screen.userinfo

import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import com.example.serviceapplication.viewModel.OidcViewModel

@Composable
fun UserInfoScreen(
    oidcViewModel: OidcViewModel,
    navigateToMain: () -> Unit
) {
    val scaffoldState = rememberScaffoldState()

    Scaffold (
        scaffoldState = scaffoldState,
        topBar = {
            UserInfoAppBar(
                navigateToMain = navigateToMain
            )
        },
        content = {
            UserInfoContent(
                oidcViewModel = oidcViewModel
            )
        }
    )
}

