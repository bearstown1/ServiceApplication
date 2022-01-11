package com.example.serviceapplication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.serviceapplication.service.OidcService
import com.example.serviceapplication.ui.theme.ServiceApplicationTheme
import com.example.serviceapplication.utils.ConnectionState
import com.example.serviceapplication.utils.currentConnectivityState
import com.example.serviceapplication.utils.log
import com.example.serviceapplication.utils.observeConnectivityAsFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        log( "MainActivity.onCreate()")

        val intent = Intent( applicationContext, OidcService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        setContent {
            ServiceApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android Test222 ")
                }
            }
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