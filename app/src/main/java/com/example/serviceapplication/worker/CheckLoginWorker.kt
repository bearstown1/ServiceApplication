package com.example.serviceapplication.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.serviceapplication.data.net.repository.OidcServerRepository
import com.example.serviceapplication.data.repository.AuthResponseInfoRepository
import com.example.serviceapplication.event.OidcEvent
import com.example.serviceapplication.event.OidcEventBus
import com.example.serviceapplication.utils.log
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltWorker
class CheckLoginWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
): CoroutineWorker(context, params) {

    private val coroutineScope = ProcessLifecycleOwner.get().lifecycleScope

    @Inject
    lateinit var oidcEventBus: OidcEventBus

    @Inject
    lateinit var authResponseInfoRepository: AuthResponseInfoRepository

    @Inject
    lateinit var oidcServerRepository: OidcServerRepository

    private var idToken:String? = null

    init {
        coroutineScope.launch(Dispatchers.IO) {
            authResponseInfoRepository.get().collect {
                if (it != null) {
                    idToken = it.idToken
                }
            }
        }
    }

    override suspend fun doWork(): Result {
        log("CheckLoginWorker.doWork()")

        var isValid:String? = null

        try {
            if (idToken != null) {
                val loginValidInfo = oidcServerRepository.checkLoginWithIdToken(idToken = idToken!!)

                val isValid = loginValidInfo?.dataMap?.get("valid").toString()

                if (!isValid.toBoolean()) {
                    oidcEventBus.provideEvent(OidcEvent.LOGOUT_BY_WORKER_EVENT)
                }
            }

        } catch (e:Exception) {
            isValid = null

        } finally {
            if (isValid.toBoolean() == false) {

                return Result.success()

            } else {

                return Result.failure()
            }
        }

    }

    companion object {
        const val WORKER_NAME = "CHECK_LOGIN_WORKER"
        const val PARAM_KEY_ID_TOKEN = "PARAM_KEY_ID_TOKEN"
    }
}