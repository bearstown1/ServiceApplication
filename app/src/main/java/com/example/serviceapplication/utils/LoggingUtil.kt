package com.example.serviceapplication.utils

import android.util.Log

fun log(message: String) {
    Log.d("BANDI_OIDC", message)
}

fun logError( message: String) {
    Log.e( "BANDI_OIDC", message)
}