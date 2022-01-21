package com.example.serviceapplication.data.net

import com.example.serviceapplication.data.model.IdTokenValidResponseInfo
import com.example.serviceapplication.data.model.UserInfo
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface OidcServerApi {

    @FormUrlEncoded
    @POST("api/oidc/userinfo")
    suspend fun getUserInfo(@Field("access_token") accessToken: String) : String

    @FormUrlEncoded
    @POST("api/sso/login/check")
    suspend fun checkLoginWithIdToken(
        @Field("id_token") idToken: String,
        @Field("iscltapi") iscltapi:String
    ): IdTokenValidResponseInfo
}