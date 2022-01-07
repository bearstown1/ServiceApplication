package com.example.serviceapplication.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.serviceapplication.service.OidcService
import com.example.serviceapplication.utils.log

class RestartReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        log("RestartReceiver.onReceive()")

        if (Intent.ACTION_BOOT_COMPLETED == intent?.action) {
            val intent = Intent(context, OidcService::class.java)

            if (context != null) {
                if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    log( "RestartReceiver.startForegroundService(intent)")

                    context.startForegroundService(intent)
                } else {
                    log( "RestartReceiver.context.startService(intent)")

                    context.startService(intent)
                }
            }
        }
    }
}