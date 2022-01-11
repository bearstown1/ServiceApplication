package com.example.serviceapplication.data.net.repository

import com.example.serviceapplication.data.model.IdTokenValidResponseInfo
import com.example.serviceapplication.data.model.UserInfo
import com.example.serviceapplication.data.net.OidcServerApi
import com.example.serviceapplication.utils.logError
import javax.inject.Inject

class OidcServerRepositoryImpl @Inject constructor(private val oidcServerApi: OidcServerApi) : OidcServerRepository{
    override suspend fun getUserInfo(accessToken: String): UserInfo? {
        try {

            return oidcServerApi.getUserInfo(accessToken)
        } catch (t : Throwable) {
            logError(t.message.toString())

            return null
        }
    }

    override suspend fun checkLoginWithIdToken(idToken: String): IdTokenValidResponseInfo? {
        try {

            return oidcServerApi.checkLoginWithIdToken(idToken,"Y")
        } catch (t : Throwable) {
            logError(t.message.toString())

            return null
        }
    }

}