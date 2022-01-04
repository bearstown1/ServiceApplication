package com.example.serviceapplication.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.serviceapplication.IOidcAidlInterface

class OidcService : Service() {

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