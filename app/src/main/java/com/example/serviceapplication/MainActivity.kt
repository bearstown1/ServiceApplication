package com.example.serviceapplication

import android.app.Activity
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.serviceapplication.data.AppStatus
import com.example.serviceapplication.data.model.AuthResponseInfo
import com.example.serviceapplication.data.repository.AuthResponseInfoRepository
import com.example.serviceapplication.data.repository.DataStoreRepository
import com.example.serviceapplication.notification.ACTION_TYPE_KEY
import com.example.serviceapplication.notification.ACTION_TYPE_VALUE_LOGIN
import com.example.serviceapplication.notification.ACTION_TYPE_VALUE_LOGOUT
import com.example.serviceapplication.notification.getNotification
import com.example.serviceapplication.service.OidcService
import com.example.serviceapplication.appauth.OidcConfig
import com.example.serviceapplication.appauth.OidcHandler
import com.example.serviceapplication.ui.navigation.NavigationHost
import com.example.serviceapplication.ui.navigation.Navigator
import com.example.serviceapplication.ui.theme.ServiceApplicationTheme
import com.example.serviceapplication.utils.ConnectionState
import com.example.serviceapplication.utils.currentConnectivityState
import com.example.serviceapplication.utils.log
import com.example.serviceapplication.utils.observeConnectivityAsFlow
import com.example.serviceapplication.viewModel.OidcViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import javax.inject.Inject

@ExperimentalComposeUiApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

    private val oidcViewModel: OidcViewModel by viewModels()

    private var notificationManager: NotificationManager? = null

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    @Inject
    lateinit var oidcHandler: OidcHandler

    @Inject
    lateinit var authResponseInfoRepository: AuthResponseInfoRepository

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

                        startLogin()
                    },
                    logoutBtnClicked = {
                        log( "logout button is clicked@@@")

                        startLogout()
                    },
                    saveOidcServerUrlBtnClicked  = {
                        log( "save url button is clicked###")

                        saveOidcServerUrl()
                    }
                )
            }
        }

        observeAppStatus()

    }

    // -- Register Server url-----------------------------------------------------------------------
    private fun saveOidcServerUrl() {
        lifecycleScope.launch {
            oidcViewModel.isShowProgressBar.value = true

            delay(100)

            OidcConfig.issuer = Uri.parse(oidcViewModel.oidcServerUrl.value)

            oidcHandler.registerIfRequired { response, ex ->
                when {
                    response != null -> {

                        if (response.authorizationEndpoint == null) {
                            // ???????????? ????????? ?????? ?????????, ?????? ??? .well-known/openid-configuration ?????? ??? ????????? ?????? ??????.
                            oidcViewModel.setSetupError(
                                getString( R.string.error_server_configuration_invalid),
                                getString( R.string.error_server_configuration_invalid_desc)
                            )

                        } else {

                            lifecycleScope.launch {

                                // register????????? ?????????, url??? saved url?????? ????????????, ?????? ?????? ??? ??????????????? ??????
                                oidcViewModel.saveOidcServerUrl()

                                // ????????? metadata ??????. ?????? ?????????????????? ?????????.
                                oidcHandler.saveMetaData( response.toJsonString())

                                // ????????? ????????????????????? ?????? ?????? ?????? ???????????? Broadcasting??? ???????????? ???.
                                if( oidcViewModel.appStatus.value == AppStatus.LOGINED) {
                                    // todo: ????????????????????? ???????????? ????????? ?????????
                                    // oidcViewModel.saveAccessToken( null)
                                    // oidcViewModel.saveIdToken( null)

                                    sendLogoutBroadcast()
                                }

                                oidcViewModel.changeAppStatus( appStatus = AppStatus.REGISTERED)

                                navController.navigate(Navigator.SCREEN_MAIN)
                            }
                        }
                    }
                    else -> {
                        // ???????????? ????????? ?????? ?????? ?????? ??????
                        oidcViewModel.setSetupError(
                            getString(R.string.error_server_url_invalid),
                            getString(R.string.error_server_url_invalid_desc)
                        )
                    }
                }

                oidcViewModel.isShowProgressBar.value = false
            }


        }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        // Notification ?????? ?????? ????????? Action
        val actionType = intent.getStringExtra( ACTION_TYPE_KEY)
        if (actionType != null) {
            // notification ??????????????? Action ??????
            handleByNotificationAction( actionType = actionType)
        }

        // ?????? ??????????????? ????????? Action??? BroadCastReceiver??? ?????? ????????? ??????
        val remoteActionType = intent.getStringExtra("remote_action_type")
        if ( remoteActionType != null) {
            handleByClientApp( remoteActionType)
        }
    }

    private fun handleByClientApp( remoteActionType: String) {

        // todo: ?????? ???????????? ?????? ?????? ?????? ??????

        if( "com.example.serviceapplication.REMOTE_ACTION_LOGIN" == remoteActionType) {
            if (oidcViewModel.appStatus.value == AppStatus.LOGINED) {
                // nothing todo
            } else {
                log( "login requested by broadcast receiver !!")
                startLogin()

            }
        } else if( "com.example.serviceapplication.REMOTE_ACTION_LOGOUT" == remoteActionType) {
            log( "logout requested by broadcast receiver @@")
            startLogout()
        }
    }


    private fun observeAppStatus() {

        lifecycleScope.launch {

            oidcViewModel.appStatus.collect { appStatus ->

                when (appStatus) {

                    AppStatus.INIT -> {

                    }

                    AppStatus.REGISTERED -> {
                        notifyAppStatus( appStatus)
                    }

                    AppStatus.LOGINED -> {
                        sendLoginBroadcast()
                        notifyAppStatus( appStatus)
                    }

                    AppStatus.LOGOUTED -> {
                        sendLogoutBroadcast()
                        notifyAppStatus( appStatus)
                    }
                }
            }
        }
    }

    private fun sendLoginBroadcast() {

        val intent = Intent()

        intent.action = "com.example.serviceapplication.BANDI_OIDC_LOGIN"

        // todo : ?????? Token ??? ?????? ????????? ???.
        intent.putExtra("ID_TOKEN", "TEST_ID_TOKEN")

        sendBroadcast(intent)

    }

    private fun sendLogoutBroadcast() {

        val intent = Intent()
        intent.action = "com.example.serviceapplication.BANDI_OIDC_LOGOUT"

        sendBroadcast(intent)
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

        // foregroundService??? ?????? ?????? action ??? login ?????? logout ??? ??????, ?????? action??? ??????
        if( ACTION_TYPE_VALUE_LOGIN == actionType) {

            log( "login requested!!")
            startLogout()

        } else if ( ACTION_TYPE_VALUE_LOGOUT == actionType) {

            log( "logout requested@@")
            startLogout()
        }
    }


    private fun changeAppStatus( appStatus: AppStatus) {
        lifecycleScope.launch {
            oidcViewModel.changeAppStatus( appStatus = appStatus)
        }
    }

    private var loginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result -> {}

        // openLoginBrowserForResult() ????????? ChromTab?????? ?????? ????????? ?????? ???, ????????? ?????? ?????? ??????
        if (result.resultCode == RESULT_OK) {

            endLogin( result.data!!)
        }
    }

    private fun startLogin() {
        oidcViewModel.initMainError()

        val intent = oidcHandler.getAuthorizationRedirectIntent()

        loginLauncher.launch(intent)
    }

    private fun endLogin(data: Intent) {
        var authorizationResponse: AuthorizationResponse? = AuthorizationResponse.fromIntent(data)
        var ex: AuthorizationException? = AuthorizationException.fromIntent(data)

        if (authorizationResponse == null) {
            oidcViewModel.setMainError(getString(R.string.error_authorization_request), ex?.errorDescription.toString())
        } else {
            Log.i(ContentValues.TAG, "Authorization response received successfully")
            Log.d(ContentValues.TAG, "CODE: ${authorizationResponse.authorizationCode}, STATE: ${authorizationResponse.state}")

            lifecycleScope.launch {
                if (authorizationResponse.authorizationCode != null) {
                    oidcViewModel.showSnackBar.value = true

                    var returnedTokenResponse = oidcHandler.redeemCodeForTokens(authorizationResponse)

                    if(returnedTokenResponse != null ){
                        val authResponseInfo = AuthResponseInfo( 1, returnedTokenResponse?.idToken, returnedTokenResponse?.accessToken, System.currentTimeMillis().toString())

                        authResponseInfoRepository.insert( authResponseInfo = authResponseInfo)

                        oidcViewModel.changeAppStatus( appStatus = AppStatus.LOGINED)
                    }

                }
            }

        }

    }

    private val logoutLancher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            endLogout(result.data!!)
        }
    }

    private fun startLogout() {
        oidcViewModel.initMainError()
        val intent = oidcHandler.getEndSessionRedirectIntent()
        this.logoutLancher.launch(intent)
    }

    private fun endLogout(data : Intent) {
        var exception : AuthorizationException? = null

        if (data != null) {
            exception = AuthorizationException.fromIntent(data)
        }

        if (exception != null) {
            oidcViewModel.setMainError(getString(R.string.error_end_session_request), exception.errorDescription.toString())
        } else {
            lifecycleScope.launch {
                removeAuthResponseInfo()

                oidcViewModel.changeAppStatus(AppStatus.LOGOUTED)

                oidcViewModel.showSnackBar.value = true
            }
        }
    }

    private suspend fun removeAuthResponseInfo() {
        val authResponseInfo = AuthResponseInfo(1,"","","")

        authResponseInfoRepository.delete(authResponseInfo)
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

