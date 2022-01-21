package com.example.serviceapplication.viewModel

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.webkit.URLUtil
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serviceapplication.R
import com.example.serviceapplication.data.AppStatus
import com.example.serviceapplication.data.model.IdTokenInfo
import com.example.serviceapplication.data.model.UserInfo
import com.example.serviceapplication.data.net.repository.OidcServerRepository
import com.example.serviceapplication.data.repository.AuthResponseInfoRepository
import com.example.serviceapplication.data.repository.DataStoreRepository
import com.example.serviceapplication.appauth.OidcConfig
import com.example.serviceapplication.utils.RequestState
import com.example.serviceapplication.utils.log
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.consumer.InvalidJwtException
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class OidcViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val authResponseInfoRepository: AuthResponseInfoRepository,
    @ApplicationContext private val context: Context,
) :ViewModel(){

    var accessToken: String? = null

    private var _appStatus = MutableStateFlow(AppStatus.INIT)
    var appStatus: StateFlow<AppStatus> = _appStatus

    fun init() {
        viewModelScope.launch( Dispatchers.IO) {

            dataStoreRepository.readUserInfo.stateIn(viewModelScope).collect {
                if(it != null && it.isNotEmpty()) {
                    val gson = Gson()
                    userInfo = gson.fromJson(it,UserInfo::class.java)
                }
            }
        }


        viewModelScope.launch( Dispatchers.IO) {
            dataStoreRepository.readOidcServerUrl.stateIn(viewModelScope).collect { it ->

                log( "dataStoreRepository.readOidcServerUrl : ${it}")

                if (it != null) {
                    oidcServerUrl.value = it

                    savedOidcServerUrl = it
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            authResponseInfoRepository.get().stateIn(viewModelScope).collect {
                if( it != null ){
                    it.let {
                        if(it.idToken != null ) {
                            setIdTokenInfo(it.idToken.toString())
                        }

                        accessToken = it.accessToken
                    }
                }
            }

        }

    }

    private fun setIdTokenInfo(idToken : String) {
        val claims = readIdTokenClaims(idToken = idToken)

        if ( claims != null) {

            val recordIdTokenInfo = IdTokenInfo(
                jwtId = claims.jwtId,
                issuer = claims.issuer,
                subject = claims.subject,
                issuedAt = claims.issuedAt.toString(),
                expirationTime = claims.expirationTime.toString(),
                audience = claims.audience.toString()
            )

            idTokenInfo.value = recordIdTokenInfo
        } else {
            idTokenInfo.value = null
        }
    }

    private fun readIdTokenClaims(idToken: String) : JwtClaims? {
        val jwtConsumer = JwtConsumerBuilder()
            .setSkipSignatureVerification()
            .setRequireSubject()
            .setAllowedClockSkewInSeconds(30)
            //.setExpectedIssuer( metadata?.discoveryDoc?.issuer)
            // todo : client id 처리
            .setExpectedAudience( OidcConfig.CLIENT_ID)
            // todo : 파싱만 가능한지..
            .setSkipAllValidators()
            .build()

        try {

            return jwtConsumer.processToClaims(idToken)

        } catch (e: InvalidJwtException) {
            Log.e(ContentValues.TAG, "${e.message}")

            return null
        }
    }

    val idTokenInfo: MutableState<IdTokenInfo?> = mutableStateOf( null)

    val oidcServerUrl: MutableState<String> = mutableStateOf( "")
    var savedOidcServerUrl:String? = ""

    // Setup ProgressBar ---------------------------------------------------------------------------
    val isShowProgressBar = mutableStateOf(false)

    // setup screen error --------------------------------------------------------------------------
    val setupErrorTitle: MutableState<String> = mutableStateOf( "")
    val setupErrorDesc: MutableState<String> = mutableStateOf( "")

    fun setSetupError( errorTitle:String, errorDescription: String) {
        setupErrorTitle.value = errorTitle
        setupErrorDesc.value = errorDescription
    }

    fun initSetupError( ) {
        setupErrorTitle.value = ""
        setupErrorDesc.value = ""
    }

    suspend fun saveOidcServerUrl() {
        withContext( Dispatchers.IO) {
            dataStoreRepository.persistOidcServerUrl( oidcServerUrl.value)
        }
    }

    fun updateOidcServerUrlAtScreen( newUrl: String) {
        oidcServerUrl.value = newUrl?.trim()
    }

    fun validateOidcServerUrl() : String? {
        if ( oidcServerUrl.value.isEmpty()) {
            return context.getString( R.string.error_server_url_empty)
        }

        if ( ! URLUtil.isValidUrl( oidcServerUrl.value)) {
            return context.getString( R.string.error_server_url_format)
        }

        return null
    }
    suspend fun changeAppStatus(appStatus: AppStatus) {
        withContext(Dispatchers.IO) {
            dataStoreRepository.persistAppStatus(appStatus = appStatus)
        }
    }

    // Main Content --------------------------------------------------------------------------------
    val mainErrorTitle: MutableState<String> = mutableStateOf( "")
    val mainErrorDesc: MutableState<String> = mutableStateOf( "")

    fun setMainError( errorTitle:String, errorDescription: String) {
        mainErrorTitle.value = errorTitle
        mainErrorTitle.value = errorDescription
    }

    fun initMainError( ) {
        mainErrorTitle.value = ""
        mainErrorDesc.value = ""
    }

    // snack bar -----------------------------------------------------------------------------------
    var showSnackBar : MutableState<Boolean> = mutableStateOf( false)


    var userInfo: UserInfo? = null


}