package com.example.serviceapplication.data.net.repository

import com.example.serviceapplication.data.model.IdTokenValidResponseInfo
import com.example.serviceapplication.data.model.UserInfo

interface OidcServerRepository {

    suspend fun getUserInfo( accessToken: String): UserInfo?

    suspend fun checkLoginWithIdToken( idToken: String): IdTokenValidResponseInfo?
}