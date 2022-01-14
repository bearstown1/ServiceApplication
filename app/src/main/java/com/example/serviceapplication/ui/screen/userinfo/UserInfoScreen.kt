package com.example.serviceapplication.ui.screen.userinfo

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.example.serviceapplication.ui.component.ColumnDivider
import com.example.serviceapplication.viewModel.OidcViewModel

@Composable
fun UserInfoScreen(
    oidcViewModel: OidcViewModel,
    navigateToMain: () -> Unit
) {
    val appStatus by oidcViewModel.appStatus.collectAsState()

    Column() {
        Text(text = "userInfo - ${ appStatus.name}")

        ColumnDivider()

        Button(onClick = {
            navigateToMain()
        }) {
            Text(text = "goToMain")
        }

        ColumnDivider()
    }
}
