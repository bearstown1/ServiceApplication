package com.example.serviceapplication

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.serviceapplication.data.AppStatus
import com.example.serviceapplication.notification.ACTION_TYPE_KEY
import com.example.serviceapplication.notification.ACTION_TYPE_VALUE_LOGIN
import com.example.serviceapplication.notification.ACTION_TYPE_VALUE_LOGOUT
import com.example.serviceapplication.notification.getNotification
import com.example.serviceapplication.service.OidcService
import com.example.serviceapplication.ui.navigation.NavigationHost
import com.example.serviceapplication.ui.theme.ServiceApplicationTheme
import com.example.serviceapplication.utils.ConnectionState
import com.example.serviceapplication.utils.currentConnectivityState
import com.example.serviceapplication.utils.log
import com.example.serviceapplication.utils.observeConnectivityAsFlow
import com.example.serviceapplication.viewModel.OidcViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

    private val coroutineScope = CoroutineScope( Dispatchers.Main.immediate)

    private val oidcViewModel: OidcViewModel by viewModels()

    private var notificationManager: NotificationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        log( "MainActivity.onCreate()")

        oidcViewModel.init()

        val intent = Intent( applicationContext, OidcService::class.java)
        intent.putExtra(OidcService.APP_STATUS_KEY, oidcViewModel.appStatus.value.name)

        notificationManager = getSystemService( Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }

        setContent {
            ServiceApplicationTheme {
                navController = rememberNavController()
                
                NavigationHost(
                    navController = navController,
                    oidcViewModel = oidcViewModel,
                    loginBtnClicked = {
                        log("login button is clicked")

                        changeAppStatus( AppStatus.LOGINED)
                    },
                    logoutBtnClicked = {
                        log( "logout button is clicked@@@")

                        changeAppStatus( AppStatus.LOGOUTED)
                    },
                    saveUrlBtnClicked = {
                        log( "save url button is clicked###")

                        changeAppStatus( AppStatus.REGISTERED)
                    }
                )
            }
        }

        observeAppStatus()

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val actionType = intent.getStringExtra(ACTION_TYPE_KEY)
        if (actionType != null) {
            // notification 으로부터의 Action 처리
            handleByNotificationAction( actionType = actionType)
        }
    }

    private fun observeAppStatus() {

        coroutineScope.launch {

            oidcViewModel.appStatus.collect { appStatus ->

                when (appStatus) {

                    AppStatus.INIT -> {

                    }

                    AppStatus.REGISTERED -> {
                        notifyAppStatus( appStatus)
                    }

                    AppStatus.LOGINED -> {
                        notifyAppStatus( appStatus)
                    }

                    AppStatus.LOGOUTED -> {
                        notifyAppStatus( appStatus)
                    }
                }
            }
        }
    }

    private fun notifyAppStatus( appStatus: AppStatus) {
        if ( OidcService.IS_RUNNING) {
            notificationManager?.notify(
                OidcService.SERVICE_ID,
                getNotification(
                    applicationContext,
                    appStatus.name
                )
            )
        }
    }


    private fun handleByNotificationAction( actionType: String) {

        // foregroundService로 부터 받은 action 이 login 또는 logout 일 경우, 해당 action을 수행
        if( ACTION_TYPE_VALUE_LOGIN == actionType) {

            log( "login requested!!")
            changeAppStatus( AppStatus.LOGINED)

        } else if ( ACTION_TYPE_VALUE_LOGOUT == actionType) {

            log( "logout requested@@")
            changeAppStatus( AppStatus.LOGOUTED)
        }
    }


    private fun changeAppStatus( appStatus: AppStatus) {
        coroutineScope.launch {
            oidcViewModel.changeAppStatus( appStatus = appStatus)
        }
    }
}

@Composable
fun Greeting(name: String) {

    Column() {
        Text(text = "Hello $name!")

        ConnectivityStatus()
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ServiceApplicationTheme {
        Greeting("Android")
    }
}


@Composable
fun currentConnectionState(): ConnectionState {
    val context = LocalContext.current
    return remember { context.currentConnectivityState }
}

@ExperimentalCoroutinesApi
@Composable
fun connectivityState(): State<ConnectionState> {
    val context = LocalContext.current
    return produceState(initialValue = context.currentConnectivityState) {
        context.observeConnectivityAsFlow().distinctUntilChanged().collect {
            value = it
        }
    }
}

@ExperimentalCoroutinesApi
@Composable
fun ConnectivityStatus() {
    // This will cause re-composition on every network state change
    val connection by connectivityState()

    val isConnected = connection === ConnectionState.Available

    if (isConnected) {
        Text(text = "Network is Available!!")
    } else {
        Text(text = "Network is Unavailable@@")
    }
}