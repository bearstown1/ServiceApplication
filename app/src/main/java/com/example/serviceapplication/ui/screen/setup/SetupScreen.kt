package com.example.serviceapplication.ui.screen.setup

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.serviceapplication.ui.component.ColumnDivider
import com.example.serviceapplication.viewModel.OidcViewModel

@Composable
fun SetupScreen(
    oidcViewModel: OidcViewModel,
    navigateToMain: () -> Unit,
    saveUrlBtnClicked: () -> Unit
) {
    val appStatus by oidcViewModel.appStatus.collectAsState()

    Column() {
        Text(text = "setup - ${ appStatus.name}")

        ColumnDivider()

        Button(onClick = {
            navigateToMain()
        }) {
            Text(text = "goToMain")
        }

        ColumnDivider()

        Button(onClick = {
            saveUrlBtnClicked()
        }) {
            Text(text = "saveUrl")
        }

        ColumnDivider()

    }
}