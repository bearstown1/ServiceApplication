package com.example.serviceapplication.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.example.serviceapplication.IOidcAidlInterface
import com.example.serviceapplication.R
import com.example.serviceapplication.event.OidcEvent
import com.example.serviceapplication.event.OidcEventBus
import com.example.serviceapplication.receiver.AlarmReceiver
import com.example.serviceapplication.utils.log
import com.example.serviceapplication.worker.CheckLoginWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class OidcService : Service() {
    @Inject
    lateinit var oidcEventBus: OidcEventBus

    private lateinit var notificationManager: NotificationManager
    private lateinit var alarmManager: AlarmManager

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        const val CHANNEL_ID: String = "OIDC_CHANNEL"
        private const val CHANNEL_NAME: String = "BANDI_OIDC_CHANNEL"

        const val SERVICE_ID: Int = 1
    }

    override fun onCreate() {
        super.onCreate()

        log( "OidcService.onCreate()")

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        unregisterRestartAlarm()

        createNotificationChannel()

        isNotificationChannelEnabled()

        startPeriodicCheckToken()

        setupEventBusSubscriber()
    }

    private fun startPeriodicCheckToken() {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val inputData = Data.Builder().putString(CheckLoginWorker.PARAM_KEY_ID_TOKEN, "TEST").build()

        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            CheckLoginWorker::class.java,
            15,
            java.util.concurrent.TimeUnit.MINUTES

        ).setInputData(inputData).setConstraints(constraints).build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            CheckLoginWorker.WORKER_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicWorkRequest
        )
    }

    private fun setupEventBusSubscriber() {
        scope.launch {
            oidcEventBus.subscribeEvent( OidcEvent.LOGOUT_BY_WORKER_EVENT,) {
                log("oidcEventBus.subscribeEvent()")
                logoutByWorkerEvent()
            }
        }
    }

    private fun logoutByWorkerEvent() {
        log( "logoutByWorkerEvent")
    }



    fun stopPeriodicCheckToken() {
        WorkManager.getInstance( applicationContext).cancelUniqueWork( CheckLoginWorker.WORKER_NAME)
    }




    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )

            channel.lightColor = Color.BLUE
            channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE

            notificationManager.createNotificationChannel( channel)
        }


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        log("OidcService.onStartCommand()")

        val bigTextStyle = NotificationCompat.BigTextStyle().setBigContentTitle("빅컨텐츠타이틀").bigText("빅텍스트")

        val notificationBuilder = NotificationCompat.Builder(
            applicationContext, CHANNEL_ID
        )
            .setStyle(bigTextStyle)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_baseline_arrow_right_alt_24)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)
            .setOngoing(true)

        startForeground(SERVICE_ID, notificationBuilder.build())

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

        val restartPendingIntent = PendingIntent.getBroadcast(this,0,intent,0)

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

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }
}