package com.example.serviceapplication.ui.screen.userinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.serviceapplication.data.AppStatus
import com.example.serviceapplication.data.model.UserInfo
import com.example.serviceapplication.ui.component.ColumnDivider
import com.example.serviceapplication.ui.component.RecordTextView
import com.example.serviceapplication.ui.theme.LARGE_PADDING
import com.example.serviceapplication.utils.RequestState
import com.example.serviceapplication.viewModel.OidcViewModel

@Composable
fun UserInfoContent(
    oidcViewModel: OidcViewModel
) {
    val appStatus by oidcViewModel.appStatus.collectAsState()

    LaunchedEffect(key1 = appStatus) {
        if(AppStatus.LOGINED == appStatus) {
            oidcViewModel.getUserInfo()
        }
    }

    var record: UserInfo? = null
    var ex: Throwable? = null



    val userInfo by oidcViewModel.userInfo.collectAsState()

    if( userInfo is RequestState.Success) {
        record = (userInfo as RequestState.Success<UserInfo>).data
    } else if ( userInfo is RequestState.Error) {
        ex = ( userInfo as RequestState.Error).error
    }

    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        if ( record != null){
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.background)
                    .padding(all = LARGE_PADDING)
                    .verticalScroll(scrollState)
            ) {

                RecordTextView( "ID", record.sub)

                ColumnDivider()

                RecordTextView( "Name", record.name)

                ColumnDivider()

                if (record.phone_number != null) {
                    RecordTextView("Phone", record.phone_number)

                    ColumnDivider()
                }
            }
        } else {
            if (ex != null) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = ex.message.toString(),
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.subtitle1,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}