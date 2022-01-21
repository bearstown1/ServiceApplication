package com.example.serviceapplication.appauth

import android.content.ContentValues
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.serviceapplication.data.model.AuthResponseInfo
import com.example.serviceapplication.data.repository.AuthResponseInfoRepository
import com.example.serviceapplication.data.repository.DataStoreRepository
import com.example.serviceapplication.utils.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.openid.appauth.*
import java.lang.Exception
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class OidcHandler @Inject constructor(
    private val authResponseInfoRepository: AuthResponseInfoRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val authorizationService: AuthorizationService
){
    private var authResponseInfo:AuthResponseInfo? = null

    private val coroutineScope = ProcessLifecycleOwner.get().lifecycleScope

    private var authState : AuthState? = null

    init {
        coroutineScope.launch( Dispatchers.IO) {
            loadMetaData()
        }

        coroutineScope.launch(Dispatchers.IO) {
            authResponseInfoRepository.get().stateIn(coroutineScope).collect {
                log("token info: ${it.toString()}")
                authResponseInfo = it
            }
        }
    }
    suspend fun saveMetaData (json:String?) {
        withContext(Dispatchers.IO) {
            if ( json != null) {

                dataStoreRepository.persistMetadata(json = json)
            }
        }
    }

    suspend fun loadMetaData() {
        withContext( Dispatchers.IO) {
            dataStoreRepository.readMetadata.stateIn(coroutineScope).collect {
                log("dataStoreRepository.readAppStatus : ${it}")

                if (it != null) {
                    val config = AuthorizationServiceConfiguration.fromJson(it)
                    authState = AuthState(config)
                }
            }
        }
    }
    suspend fun registerIfRequired(handleResponse: (response: AuthorizationServiceConfiguration?, ex: Exception?) -> Unit) {
        withContext(Dispatchers.IO) {
            if (OidcConfig.issuer != null) {
                AuthorizationServiceConfiguration.fetchFromIssuer(OidcConfig.issuer) { response, ex ->
                    handleResponse(response, ex)
                }
            } else {
                // todo: config.issuer가 설정되지 않았을 경우, 알림 처리
            }

            /* 임시 주석 처리
                if (registrationResponse == null) {
                    registrationResponse = registerClient(metadata!!)
                }
            */
        }
    }

    fun getAuthorizationRedirectIntent() : Intent? {
        val extraParams = mutableMapOf<String,String>()

        var clientId = OidcConfig.CLIENT_ID

        if (authState?.authorizationServiceConfiguration != null) {
            var request = AuthorizationRequest.Builder(
                authState?.authorizationServiceConfiguration!!,
                clientId,
                ResponseTypeValues.CODE,
                OidcConfig.redirectUri
            )
                .setScope(OidcConfig.scope)
                .setAdditionalParameters(extraParams)
                .build()

            return authorizationService.getAuthorizationRequestIntent(request)

        } else {

            return null
        }

    }

    suspend fun redeemCodeForTokens(authResponse: AuthorizationResponse) : TokenResponse? {

        return suspendCoroutine { continuation ->
            val extraParams = mapOf("client_secret" to OidcConfig.CLIENT_SECRET)

            val tokenRequest = authResponse.createTokenExchangeRequest(extraParams)

            authorizationService.performTokenRequest(tokenRequest) { tokenResponse,ex ->
                when {
                    tokenResponse != null -> {
                        Log.i(ContentValues.TAG, "Authorization code grant response received successfully")
                        Log.d(ContentValues.TAG, "AT: ${tokenResponse.accessToken}, RT: ${tokenResponse.refreshToken}, IDT: ${tokenResponse.idToken}" )
                        continuation.resume(tokenResponse)
                    }
                    else -> {

                    }
                }
            }
        }

    }

    fun getEndSessionRedirectIntent() : Intent? {
        if (authState?.authorizationServiceConfiguration != null) {
            val idToken = authResponseInfo?.idToken

            if (idToken != null) {
                val extraParams = mapOf("client_id" to OidcConfig.CLIENT_ID)

                val request = EndSessionRequest.Builder(authState?.authorizationServiceConfiguration!!)
                    .setIdTokenHint(idToken)
                    .setPostLogoutRedirectUri(OidcConfig.postLogoutRedirectUri)
                    .setAdditionalParameters(extraParams)
                    .build()

                return authorizationService.getEndSessionRequestIntent(request)
            } else {
                return null
            }
        } else {
            //todo 처리
            return null
        }
    }

}