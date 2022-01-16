package com.example.serviceapplication.ui.screen.main

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import com.example.serviceapplication.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.serviceapplication.data.AppStatus
import com.example.serviceapplication.ui.theme.fabBackgroundColor
import com.example.serviceapplication.viewModel.OidcViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    oidcViewModel: OidcViewModel,
    navigateToSetup: () -> Unit,
    navigateToUserInfo: () -> Unit,
    navigateToFaq: () -> Unit,
    loginBtnClicked: () -> Unit,
    logoutBtnClicked: () -> Unit,
) {

    val scaffoldState = rememberScaffoldState()

    DisplaySnackBar(
        scaffoldState =  scaffoldState,
        oidcViewModel = oidcViewModel
    )

    Scaffold (
        scaffoldState = scaffoldState,
        topBar = {
            MainAppBar(
                oidcViewModel = oidcViewModel,
                navigateToSetup = navigateToSetup,
                navigateToUserInfo = navigateToUserInfo,
                loginBtnClicked = loginBtnClicked,
                logoutBtnClicked = logoutBtnClicked,
            )
        },
        content = {
            MainContent(
                oidcViewModel = oidcViewModel
            )
        },
        floatingActionButton = {
            MainFab( onFabClicked = navigateToFaq)
        }
    )

}

@Composable
fun MainFab(
    onFabClicked: () -> Unit
) {
    FloatingActionButton(
        onClick = {
            onFabClicked()
        },
        backgroundColor = MaterialTheme.colors.fabBackgroundColor
    ) {
        Icon( imageVector = Icons.Filled.Add,
            contentDescription = stringResource(id = R.string.faq),
            tint = Color.White
        )
    }
}

@Composable
fun DisplaySnackBar(
    scaffoldState: ScaffoldState,
    oidcViewModel: OidcViewModel
) {

    val loginStatus by oidcViewModel.appStatus.collectAsState()

    val scope = rememberCoroutineScope()

    val label = stringResource(id = R.string.ok)

    val message =  when ( loginStatus) {
        AppStatus.LOGINED -> stringResource(id = R.string.msg_logined)
        AppStatus.LOGOUTED -> stringResource(id = R.string.msg_logouted)
        else -> ""
    }

    val showSnackBar by oidcViewModel.showSnackBar

    if ( message != "") {

        LaunchedEffect(key1 = loginStatus) {

            if (showSnackBar) {

                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = label
                    )

                    oidcViewModel.showSnackBar.value = false
                }
            }
        }
    }
}