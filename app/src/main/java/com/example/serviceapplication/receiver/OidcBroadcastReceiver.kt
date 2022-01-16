package com.example.serviceapplication.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.compose.ui.ExperimentalComposeUiApi
import com.example.serviceapplication.MainActivity
import com.example.serviceapplication.utils.logError

class OidcBroadcastReceiver : BroadcastReceiver() {

    @ExperimentalComposeUiApi
    override fun onReceive(context: Context, intent: Intent) {

        val action = intent?.action

        val clientPackageName = intent?.getStringExtra( "PACKAGE_NAME")
        val clientId = intent?.getStringExtra( "client_id")
        val clientSecret = intent?.getStringExtra( "client_secret")

        val intent = action?.let {

            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("remote_action_type", action)

            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            try {
                pendingIntent.send();
            } catch (e: Throwable) {
                logError( e.message.toString())
            }
        }
    }
}