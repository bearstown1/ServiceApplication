package com.example.serviceapplication.ui.component

import androidx.compose.foundation.layout.height
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.serviceapplication.ui.theme.MEDIUM_PADDING

@Composable
fun ColumnDivider() {
    Divider(
        modifier = Modifier
            .height(MEDIUM_PADDING),
        color = MaterialTheme.colors.background
    )
}