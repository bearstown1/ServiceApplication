package com.example.serviceapplication.ui.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.serviceapplication.ConnectivityStatus
import com.example.serviceapplication.R
import com.example.serviceapplication.ui.component.ColumnDivider
import com.example.serviceapplication.ui.component.RecordTextView
import com.example.serviceapplication.ui.theme.LARGE_PADDING
import com.example.serviceapplication.viewModel.OidcViewModel


@Composable
fun MainContent(
    oidcViewModel: OidcViewModel
) {
    val appStatus by oidcViewModel.appStatus.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(all = LARGE_PADDING)
    ) {

        ConnectivityStatus()

        ColumnDivider()

        Card(
            shape = RoundedCornerShape( 8.dp),
            elevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.background)
                    .padding(all = LARGE_PADDING)
            ) {

                RecordTextView( stringResource( id = R.string.app_status), appStatus.name)
            }
        }

        if ( oidcViewModel.mainErrorTitle.value.isNotEmpty()) {

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = oidcViewModel.mainErrorTitle.value,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.h6,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }

        if ( oidcViewModel.mainErrorDesc.value.isNotEmpty()) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = oidcViewModel.mainErrorTitle.value,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.subtitle1,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}