package com.example.serviceapplication.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.serviceapplication.R
import com.example.serviceapplication.ui.theme.MEDIUM_PADDING
import com.example.serviceapplication.ui.theme.topAppBarContentColor

@Composable
fun ColumnDivider() {
    Divider(
        modifier = Modifier
            .height(MEDIUM_PADDING),
        color = MaterialTheme.colors.background
    )
}
@Composable
fun BackAction(
    navigateToMain: () -> Unit
) {
    IconButton( onClick = { navigateToMain()}) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = stringResource(id = R.string.back_arrow),
            tint = MaterialTheme.colors.topAppBarContentColor
        )
    }
}

@Composable
fun RecordTextView(
    label: String,
    value: String
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = {},
        label = {
            Text( text = label)
        },
        textStyle = MaterialTheme.typography.body1,
        singleLine = true,
        enabled = false
    )
}