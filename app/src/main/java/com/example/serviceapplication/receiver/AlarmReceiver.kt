package com.example.serviceapplication.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.serviceapplication.service.OidcService
import com.example.serviceapplication.utils.log

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val intent = Intent(context, OidcService::class.java)

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            log( "AlarmReceiver.startForegroundService(intent)")

            context?.startForegroundService(intent)
        } else {
            log( "AlarmReceiver.context.startService(intent)")

            context?.startService(intent)
        }
    }
}