package com.example.serviceapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.serviceapplication.viewModel.OidcViewModel
import com.example.serviceapplication.ui.navigation.Navigator.Companion.SCREEN_MAIN
import com.example.serviceapplication.ui.navigation.destination.mainComposable
import com.example.serviceapplication.ui.navigation.destination.setupComposable
import com.example.serviceapplication.ui.navigation.destination.userInfoComposable

@ExperimentalComposeUiApi
@Composable
fun NavigationHost(
    navController: NavHostController,
    oidcViewModel: OidcViewModel,
    loginBtnClicked: () -> Unit,
    logoutBtnClicked: () -> Unit,
    saveOidcServerUrlBtnClicked : () -> Unit
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
            navigateToFaq = navigator.toFaq,
            loginBtnClicked = loginBtnClicked,
            logoutBtnClicked = logoutBtnClicked,
        )

        setupComposable(
            oidcViewModel = oidcViewModel,
            navigateToMain = navigator.toMain,
            saveOidcServerUrlBtnClicked  = saveOidcServerUrlBtnClicked
        )

        userInfoComposable(
            oidcViewModel = oidcViewModel,
            navigateToMain = navigator.toMain
        )
    }

    val appStatus by oidcViewModel.appStatus.collectAsState()

    /*
// 최초상태값에 따른 화면 이동 처리
if ( appStatus == AppStatus.INIT) {
    navigator.toSetup()
} else {
    navigator.toMain()
}
*/
}