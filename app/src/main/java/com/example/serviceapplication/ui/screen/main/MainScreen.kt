package com.example.serviceapplication.ui.screen.main

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.serviceapplication.data.AppStatus
import com.example.serviceapplication.ui.component.ColumnDivider
import com.example.serviceapplication.utils.ConnectionState
import com.example.serviceapplication.utils.currentConnectivityState
import com.example.serviceapplication.utils.observeConnectivityAsFlow
import com.example.serviceapplication.viewModel.OidcViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun MainScreen(
    oidcViewModel: OidcViewModel,
    navigateToSetup: () -> Unit,
    navigateToUserInfo: () -> Unit,
    loginBtnClicked: () -> Unit,
    logoutBtnClicked: () -> Unit,
) {
    val appStatus by oidcViewModel.appStatus.collectAsState()

    Column() {
        Text(text = "main - ${ appStatus.name}")

        ColumnDivider()

        ConnectivityStatus()

        ColumnDivider()

        Button(onClick = {
            navigateToSetup()
        }) {
            Text(text = "goToSetup")
        }

        ColumnDivider()

        if (AppStatus.INIT != appStatus) {
            if (AppStatus.REGISTERED == appStatus || AppStatus.LOGOUTED == appStatus) {
                Button(onClick = { loginBtnClicked()}) {
                    Text(text = "login")
                }
                ColumnDivider()
            }

            if (AppStatus.LOGINED == appStatus) {

                Button(onClick = {
                    navigateToUserInfo()
                }) {
                    Text(text = "goToUserInfo")
                }

                ColumnDivider()

                Button(onClick = {
                    logoutBtnClicked()
                }) {
                    Text(text = "logout")
                }

                ColumnDivider()
            }
        }

    }


}

@Composable
fun currentConnectionState(): ConnectionState {
    val context = LocalContext.current
    return remember { context.currentConnectivityState }
}

@ExperimentalCoroutinesApi
@Composable
fun connectivityState(): State<ConnectionState> {
    val context = LocalContext.current
    return produceState(initialValue = context.currentConnectivityState) {
        context.observeConnectivityAsFlow().distinctUntilChanged().collect {
            value = it
        }
    }
}

@ExperimentalCoroutinesApi
@Composable
fun ConnectivityStatus() {
    // This will cause re-composition on every network state change
    val connection by connectivityState()

    val isConnected = connection === ConnectionState.Available

    if (isConnected) {
        Text(text = "Network is Available!!")
    } else {
        Text(text = "Network is Unavailable@@")
    }
}