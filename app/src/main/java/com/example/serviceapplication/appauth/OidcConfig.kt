package com.example.serviceapplication.appauth

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

object OidcConfig {
    var issuer : Uri = Uri.parse("")

    val redirectUri: Uri = Uri.parse("com.example.serviceapplication.aars:/oauth2redirect")
    val postLogoutRedirectUri: Uri = Uri.parse("com.example.serviceapplication.aars:/oauth2redirect")
    val scope = "openid profile"

    val metadata: MutableState<String> = mutableStateOf( "")

    const val CLIENT_ID     = "oidc_mobile_id"
    const val CLIENT_SECRET = "oyFI9TjrNiu7Y5_JsczCrWuZnKLhtTSNKzfOwZcLE7oYuscCCVgsn3xq4RrARslLxRPkO4WRpM1x6g2g115JPQ"
}