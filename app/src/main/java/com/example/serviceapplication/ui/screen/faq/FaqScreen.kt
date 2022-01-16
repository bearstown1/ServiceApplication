package com.example.serviceapplication.ui.screen.faq

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.example.serviceapplication.viewModel.OidcViewModel

@Composable
fun FaqScreen(
    oidcViewModel: OidcViewModel,
    navigateToMain: () -> Unit
) {
    Text(
        text = "faq"
    )
}