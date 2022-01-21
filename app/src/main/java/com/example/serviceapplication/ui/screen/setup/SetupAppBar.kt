package com.example.serviceapplication.ui.screen.setup

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.*
import com.example.serviceapplication.R
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import com.example.serviceapplication.data.AppStatus
import com.example.serviceapplication.ui.component.BackAction
import com.example.serviceapplication.ui.component.ConfirmDialog
import com.example.serviceapplication.ui.theme.topAppBarBackgroundColor
import com.example.serviceapplication.ui.theme.topAppBarContentColor
import com.example.serviceapplication.viewModel.OidcViewModel

@ExperimentalComposeUiApi
@Composable
fun SetupAppBar(
    oidcViewModel: OidcViewModel,
    navigateToMain: () -> Unit,
    saveOidcServerUrlBtnClicked: () -> Unit
) {

    val appStatus by oidcViewModel.appStatus.collectAsState()

    var openDialog by remember{ mutableStateOf( false) }

    val keyboardController = LocalSoftwareKeyboardController.current

    var isUpdatable by remember {
        mutableStateOf(AppStatus.INIT == appStatus)
    }

    LaunchedEffect(key1 = oidcViewModel.oidcServerUrl.value) {
        isUpdatable = oidcViewModel.oidcServerUrl.value != oidcViewModel.savedOidcServerUrl
    }

    TopAppBar(
        navigationIcon = {
            BackAction( navigateToMain = navigateToMain)
        },
        title = {
            Text(
                text = stringResource(id = R.string.setup),
                color = MaterialTheme.colors.topAppBarContentColor
            )
        },
        backgroundColor = MaterialTheme.colors.topAppBarBackgroundColor,
        actions = {

            if ( appStatus != AppStatus.INIT) {
                ConfirmDialog(
                    title = stringResource(id = R.string.confirm_change_server_url),
                    message = stringResource(id = R.string.confirm_change_server_url_confirmation ),
                    openDialog = openDialog,
                    closeDialog = {
                        openDialog = false
                    },
                    onYesClicked = {
                        keyboardController?.hide()
                        saveOidcServerUrlBtnClicked()
                    }
                )

                UpdateAction(
                    isUpdatable = isUpdatable,
                    saveOidcServerUrlBtnClicked = {
                        openDialog = true
                    }
                )

            } else {
                UpdateAction(
                    isUpdatable= isUpdatable,
                    saveOidcServerUrlBtnClicked = saveOidcServerUrlBtnClicked
                )
            }
        }
    )
}

@ExperimentalComposeUiApi
@Composable
fun UpdateAction(
    isUpdatable: Boolean,
    saveOidcServerUrlBtnClicked: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val icon = if ( isUpdatable) {
        Icons.Filled.Check
    } else {
        Icons.Filled.Lock
    }

    IconButton( onClick = {
        keyboardController?.hide()
        saveOidcServerUrlBtnClicked()
    }) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(id = R.string.update_icon),
            tint = MaterialTheme.colors.topAppBarContentColor
        )
    }
}