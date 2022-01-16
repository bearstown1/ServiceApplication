package com.example.serviceapplication.ui.screen.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.serviceapplication.R
import com.example.serviceapplication.data.AppStatus
import com.example.serviceapplication.ui.component.ConfirmDialog
import com.example.serviceapplication.ui.theme.LARGE_PADDING
import com.example.serviceapplication.ui.theme.topAppBarBackgroundColor
import com.example.serviceapplication.ui.theme.topAppBarContentColor
import com.example.serviceapplication.viewModel.OidcViewModel

@Composable
fun MainAppBar(
    oidcViewModel: OidcViewModel,
    navigateToSetup: () -> Unit,
    navigateToUserInfo: () -> Unit,
    loginBtnClicked: () -> Unit,
    logoutBtnClicked: () -> Unit,
) {
    val appStatus by oidcViewModel.appStatus.collectAsState()

    var openDialog by remember{ mutableStateOf( false) }

    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.title),
                color = MaterialTheme.colors.topAppBarContentColor
            )
        },
        actions = {
            if ( appStatus == AppStatus.LOGINED) {
                ConfirmDialog(
                    title = stringResource(id = R.string.confirm_logout_title),
                    message = stringResource(id = R.string.confirm_logout_confirmation ),
                    openDialog = openDialog,
                    closeDialog = {
                        openDialog = false
                    },
                    onYesClicked = {
                        logoutBtnClicked()
                    }
                )

                LogoutAction( logoutClicked = {
                    openDialog = true
                })
            }

            if ( appStatus == AppStatus.REGISTERED || appStatus == AppStatus.LOGOUTED) {
                LoginAction( loginBtnClicked = loginBtnClicked)
            }

            MoreAction(
                loginStatus = appStatus,
                setupClicked = {

                    navigateToSetup()
                },
                userInfoClicked = navigateToUserInfo
            )
        },
        backgroundColor = MaterialTheme.colors.topAppBarBackgroundColor
    )
}

@Composable
fun LoginAction(
    loginBtnClicked: () -> Unit
) {
    Button(
        onClick = {
            loginBtnClicked()
        },
    ) {
        Text(text = stringResource(id = R.string.login))
    }
}

@Composable
fun LogoutAction(
    logoutClicked: () -> Unit
) {
    Button(
        onClick = {
            logoutClicked()
        },
    ) {
        Text(text = stringResource(id = R.string.logout))
    }
}

@Composable
fun MoreAction(
    loginStatus: AppStatus,
    setupClicked: () -> Unit,
    userInfoClicked: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(
        onClick = {
            expanded = true
        }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_vertical_menu),
            contentDescription = stringResource(id = R.string.more),
            tint = MaterialTheme.colors.topAppBarContentColor
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            DropdownMenuItem(
                onClick = {
                    expanded = false

                    setupClicked()
                }
            ) {
                Text(
                    modifier = Modifier.padding( start = LARGE_PADDING),
                    text = stringResource(
                        id = R.string.setup
                    ),
                    style = MaterialTheme.typography.subtitle2
                )
            }

            DropdownMenuItem(
                onClick = {
                    expanded = false
                    userInfoClicked()
                }
            ) {
                Text(
                    modifier = Modifier.padding(start = LARGE_PADDING),
                    text = stringResource(
                        id = R.string.userInfo
                    ),
                    style = MaterialTheme.typography.subtitle2
                )
            }
        }

    }
}
