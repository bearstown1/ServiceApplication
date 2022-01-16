package com.example.serviceapplication.ui.screen.userinfo

import androidx.compose.material.MaterialTheme
import com.example.serviceapplication.R
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.serviceapplication.ui.component.BackAction
import com.example.serviceapplication.ui.theme.topAppBarBackgroundColor
import com.example.serviceapplication.ui.theme.topAppBarContentColor

@Composable
fun UserInfoAppBar(
    navigateToMain: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            BackAction( navigateToMain = navigateToMain)
        },
        title = {
            Text(
                text = stringResource(id = R.string.userInfo),
                color = MaterialTheme.colors.topAppBarContentColor
            )
        },
        backgroundColor = MaterialTheme.colors.topAppBarBackgroundColor
    )
}


