package com.example.serviceapplication.service.appauth

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

object OidcConfig {
    var issuer : Uri = Uri.parse("")

    val redirectUri: Uri = Uri.parse("com.example.serviceapplication.aars:/oauth2redirect")
    val postLogoutRedirectUri: Uri = Uri.parse("com.example.serviceapplication.aars:/oauth2redirect")
    val scope = "openid profile"

    val metadata: MutableState<String> = mutableStateOf( "")
}