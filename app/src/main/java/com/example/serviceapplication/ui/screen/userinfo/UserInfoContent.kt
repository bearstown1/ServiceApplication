package com.example.serviceapplication.ui.screen.userinfo

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.serviceapplication.ui.component.ColumnDivider
import com.example.serviceapplication.viewModel.OidcViewModel

@Composable
fun UserInfoContent(
    oidcViewModel: OidcViewModel
) {
    val appStatus by oidcViewModel.appStatus.collectAsState()

    Column() {
        Text(text = "userInfo - ${ appStatus.name}")

        ColumnDivider()
    }
}