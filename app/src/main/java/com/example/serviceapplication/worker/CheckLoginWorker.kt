package com.example.serviceapplication.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.serviceapplication.event.OidcEvent
import com.example.serviceapplication.event.OidcEventBus
import com.example.serviceapplication.utils.log
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.lang.Exception
import javax.inject.Inject

@HiltWorker
class CheckLoginWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
): CoroutineWorker(context, params) {

    @Inject
    lateinit var oidcEventBus: OidcEventBus

    override suspend fun doWork(): Result {
        log("CheckLoginWorker.doWork()")

        var isValid:String? = null

        try {
            var idToken = inputData?.getString(PARAM_KEY_ID_TOKEN)!!

            log("CheckLoginWorker - id token: ${idToken}")

            oidcEventBus.provideEvent(OidcEvent.LOGOUT_BY_WORKER_EVENT)

            isValid = "true"
        } catch (e:Exception) {
            isValid = null
        } finally {
            if (isValid.toBoolean() == true) {

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