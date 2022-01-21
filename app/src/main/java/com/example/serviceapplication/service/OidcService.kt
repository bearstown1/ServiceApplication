package com.example.serviceapplication.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.example.serviceapplication.IOidcAidlInterface
import com.example.serviceapplication.data.AppStatus
import com.example.serviceapplication.data.model.AuthResponseInfo
import com.example.serviceapplication.data.repository.DataStoreRepository
import com.example.serviceapplication.data.repository.AuthResponseInfoRepository
import com.example.serviceapplication.event.OidcEvent
import com.example.serviceapplication.event.OidcEventBus
import com.example.serviceapplication.notification.getNotification
import com.example.serviceapplication.receiver.AlarmReceiver
import com.example.serviceapplication.utils.log
import com.example.serviceapplication.utils.observeConnectivityAsFlow
import com.example.serviceapplication.worker.CheckLoginWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import java.util.*
import javax.inject.Inject

@ExperimentalComposeUiApi
@AndroidEntryPoint
class OidcService : LifecycleService() {
    @Inject
    lateinit var oidcEventBus: OidcEventBus

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    @Inject
    lateinit var tokenInfoRepository: AuthResponseInfoRepository

    @Inject
    lateinit var authResponseInfoRepository: AuthResponseInfoRepository

    private lateinit var notificationManager: NotificationManager
    private lateinit var alarmManager: AlarmManager

    companion object {
        const val CHANNEL_ID: String = "OIDC_CHANNEL"
        private const val CHANNEL_NAME: String = "BANDI_OIDC_CHANNEL"

        const val SERVICE_ID: Int = 1

        const val APP_STATUS_KEY = "APP_STATUS"

        var IS_RUNNING: Boolean = false
    }

    override fun onCreate() {
        super.onCreate()

        log( "OidcService.onCreate()")

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        unregisterRestartAlarm()

        createNotificationChannel()

        setupEventBusSubscriber()

        observerAppStatus()

        isNotificationChannelEnabled()

        monitoringNetworkState()
    }

    private fun observerAppStatus() {
        lifecycleScope.launch(Dispatchers.IO) {
            dataStoreRepository.readAppStatus.stateIn(lifecycleScope).collect {
                log("dataStoreRepository.readAppStatus : ${it}")

                if ( AppStatus.LOGINED.name == it) {

                    startPeriodicCheckToken()

                } else if ( AppStatus.LOGOUTED.name == it) {

                    stopPeriodicCheckToken()
                }
            }
        }
    }



    // Worker 구현 ----------------------------------------------------------------------------
    private fun startPeriodicCheckToken() {

        // todo: 제약 조건에 대해서 확인해 봐야 할 듯...
        val constraints = Constraints.Builder()
            .setRequiredNetworkType( NetworkType.CONNECTED)
            .build()

        // 기본적으로 15분에 한 번씩 물어봄
        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            CheckLoginWorker::class.java,
            15,
            java.util.concurrent.TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance( applicationContext).enqueueUniquePeriodicWork(
            CheckLoginWorker.WORKER_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicWorkRequest
        )
    }
    private fun setupEventBusSubscriber() {
        lifecycleScope.launch {
            oidcEventBus.subscribeEvent( OidcEvent.LOGOUT_BY_WORKER_EVENT,) {
                log("oidcEventBus.subscribeEvent()")
                logoutByWorkerEvent()
            }
        }
    }

    private fun logoutByWorkerEvent() {

        lifecycleScope.launch(Dispatchers.IO){
            removeAuthResponseInfo()

            dataStoreRepository.persistAppStatus(AppStatus.LOGOUTED)

        }

        sendLogoutBroadCast()

        notifyLogoutAppStatus()

    }

    private fun sendLogoutBroadCast() {
        val intent = Intent()
        intent.action = "com.example.serviceapplication.BANDI_OIDC_LOGOUT"

        sendBroadcast(intent)
    }


    private fun notifyLogoutAppStatus() {
        if ( IS_RUNNING) {
            notificationManager?.notify(
                SERVICE_ID,
                getNotification(
                    applicationContext,
                    AppStatus.LOGOUTED.name
                )
            )
        }
    }

    private suspend fun removeAuthResponseInfo() {
        val authResponseInfo = AuthResponseInfo(1,"","","")

        authResponseInfoRepository.delete(authResponseInfo)

    }



    fun stopPeriodicCheckToken() {
        WorkManager.getInstance( applicationContext).cancelUniqueWork( CheckLoginWorker.WORKER_NAME)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            channel.lightColor = Color.BLUE
            channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE

            notificationManager.createNotificationChannel( channel)
        }


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        log("OidcService.onStartCommand()")

        if(IS_RUNNING == false ) {
            val appStatus = intent?.getStringExtra(APP_STATUS_KEY)

            appStatus?.let {
                startForeground(SERVICE_ID, getNotification(applicationContext, appStatus))
            }

            IS_RUNNING = true
        }


        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        log("OidcService.onDestroy()")

        stopPeriodicCheckToken()

        registerRestartAlarm()
    }

    private fun registerRestartAlarm() {
        log("OidcService.registerRestartAlarm()")

        val c: Calendar = Calendar.getInstance()
        c.timeInMillis = System.currentTimeMillis()
        c.add(Calendar.SECOND,1)

        val restartPendingIntent = getRestartPendingIndent()

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, c.timeInMillis, restartPendingIntent)
    }

    private fun getRestartPendingIndent(): PendingIntent {
        val intent = Intent(this, AlarmReceiver::class.java)

        val restartPendingIntent = PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)

        return restartPendingIntent
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        log("OidcService.onTaskRemoved()")

        stopSelf()
    }

    private fun unregisterRestartAlarm() {
        log("OidcService.unregisterRestartAlarm()")

        val restartPendingIntent = getRestartPendingIndent()

        alarmManager.cancel(restartPendingIntent)
    }

    private fun isNotificationChannelEnabled(): Boolean {

        val notificationsEnabled = NotificationManagerCompat.from(this).areNotificationsEnabled()

        if ( notificationsEnabled) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                val channel = notificationManager.getNotificationChannel( CHANNEL_ID)

                if ( channel == null) {
                    return true
                } else {
                    val isImportance = channel.importance != NotificationManager.IMPORTANCE_NONE

                    return isImportance
                }
            }

            return true
        }

        return false
    }





    private val binder = object : IOidcAidlInterface.Stub() {
        override fun isLogined(): Boolean {
            Log.d("oidc","isLogined()")

            return true
        }

        override fun getUserInfo(): String {
            Log.d("oidc","getUserInfo()")

            return "hello world"
        }

    }

    fun monitoringNetworkState() {

        lifecycleScope.launch( Dispatchers.IO) {
            applicationContext.observeConnectivityAsFlow().stateIn( lifecycleScope).collect {
                log( "Network connection state : $it")
            }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind( intent)

        val binder = object : IOidcAidlInterface.Stub() {
            override fun isLogined(): Boolean {

                // todo: Biz Logic
                return true
            }

            override fun getUserInfo(): String {

                // todo: Biz Logic
                return "hello world"
            }
        }

        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }
}