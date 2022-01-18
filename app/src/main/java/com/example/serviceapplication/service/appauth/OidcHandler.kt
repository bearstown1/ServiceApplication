package com.example.serviceapplication.service.appauth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthorizationServiceConfiguration
import java.lang.Exception

object OidcHandler {
    suspend fun registerIfRequired(handleResponse: (response: AuthorizationServiceConfiguration?, ex: Exception?) -> Unit) {
        withContext(Dispatchers.IO) {

        }
    }
}