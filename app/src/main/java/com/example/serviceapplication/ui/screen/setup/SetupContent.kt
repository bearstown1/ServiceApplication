package com.example.serviceapplication.ui.screen.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.serviceapplication.ui.theme.LARGE_PADDING
import com.example.serviceapplication.R
import com.example.serviceapplication.ui.theme.MEDIUM_PADDING

@ExperimentalComposeUiApi
@Composable
fun SetupContent(
    oidcServerUrl: String,
    onOidcServerUrlChange: ( String) -> Unit,
    isShowProgressBar: Boolean,
    setupErrorTitle: String,
    setupErrorDesc: String,
    saveOidcServerUrlBtnClicked: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(all = LARGE_PADDING)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = oidcServerUrl,
            onValueChange = { onOidcServerUrlChange( it)},
            label = {
                Text( text = stringResource(id = R.string.url))
            },
            textStyle = MaterialTheme.typography.body1,
            singleLine = true,
            enabled = ! isShowProgressBar,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search,
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                    saveOidcServerUrlBtnClicked()
                }
            )
        )
        Divider(
            modifier = androidx.compose.ui.Modifier
                .height( MEDIUM_PADDING),
            color = MaterialTheme.colors.background
        )

        CircularIndeterminateProgressBar( isShowProgressBar)

        if ( setupErrorTitle.isNotEmpty()) {

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = setupErrorTitle,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.h6,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }

        if ( setupErrorDesc.isNotEmpty()) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = setupErrorDesc,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.subtitle1,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun CircularIndeterminateProgressBar( isShowProgressBar: Boolean) {
    if ( isShowProgressBar) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colors.primary
            )
        }
    }
}