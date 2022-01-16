package com.example.serviceapplication.ui.screen.setup

import android.content.Context
import android.widget.Toast
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import com.example.serviceapplication.ui.component.BackHandler
import com.example.serviceapplication.viewModel.OidcViewModel

@ExperimentalComposeUiApi
@Composable
fun SetupScreen(
    oidcViewModel: OidcViewModel,
    navigateToMain: () -> Unit,
    saveOidcServerUrlBtnClicked: () -> Unit
) {
    val context = LocalContext.current

    val oidcServerUrl by oidcViewModel.oidcServerUrl

    BackHandler( onBackPressed = navigateToMain)

    Scaffold(
        topBar = {
            SetupAppBar(
                oidcViewModel = oidcViewModel,
                navigateToMain = navigateToMain,
                saveOidcServerUrlBtnClicked = {
                    updateSetupInfo(
                        oidcViewModel = oidcViewModel,
                        saveOidcServerUrlBtnClicked = saveOidcServerUrlBtnClicked,
                        context = context
                    )
                }
            )
        },
        content = {
            SetupContent (
                oidcServerUrl = oidcServerUrl,
                onOidcServerUrlChange = {
                    oidcViewModel.updateOidcServerUrlAtScreen( it)
                },
                setupErrorTitle = oidcViewModel.setupErrorTitle.value,
                setupErrorDesc = oidcViewModel.setupErrorDesc.value,
                isShowProgressBar = oidcViewModel.isShowProgressBar.value,
                saveOidcServerUrlBtnClicked = {
                    updateSetupInfo(
                        oidcViewModel = oidcViewModel,
                        saveOidcServerUrlBtnClicked = saveOidcServerUrlBtnClicked,
                        context = context
                    )
                }
            )
        }
    )
}

fun updateSetupInfo(
    oidcViewModel: OidcViewModel,
    context: Context,
    saveOidcServerUrlBtnClicked: () -> Unit){

    val errorMsg = oidcViewModel.validateOidcServerUrl()

    if ( errorMsg == null) {

        oidcViewModel.initSetupError()

        saveOidcServerUrlBtnClicked()

    } else {
        displayToast( context, errorMsg)
    }
}


fun displayToast(context: Context, errorMsg: String) {
    Toast.makeText(
        context,
        errorMsg,
        Toast.LENGTH_SHORT
    ).show()
}
