package com.example.serviceapplication.ui.component

import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.serviceapplication.utils.ConnectionState
import com.example.serviceapplication.utils.currentConnectivityState
import com.example.serviceapplication.utils.observeConnectivityAsFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn


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
        val coroutineScope = ProcessLifecycleOwner.get().lifecycleScope

        context.observeConnectivityAsFlow().distinctUntilChanged().stateIn(coroutineScope).collect {
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