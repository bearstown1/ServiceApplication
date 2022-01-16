package com.example.serviceapplication.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.app.NotificationCompat
import com.example.serviceapplication.MainActivity
import com.example.serviceapplication.R
import com.example.serviceapplication.data.AppStatus
import com.example.serviceapplication.service.OidcService

const val ACTION_TYPE_KEY                   = "ACTION_TYPE_KEY"
const val ACTION_TYPE_VALUE_OPEN_APP        = "AT_VALUE_OPEN_APP"
const val ACTION_TYPE_VALUE_LOGIN           = "AT_VALUE_LOGIN"
const val ACTION_TYPE_VALUE_LOGOUT          = "AT_VALUE_LOGOUT"

@ExperimentalComposeUiApi
fun getNotification(context: Context, appStatus: String?) : Notification {
    var description:String? = null
    var smallIcon :Int = -1

    when (appStatus) {
        AppStatus.INIT.name -> {
            description = context.getString(R.string.msg_need_setup)
            smallIcon = R.drawable.ic_logout
        }

        AppStatus.REGISTERED.name -> {
            description = context.getString( R.string.msg_registered)
            smallIcon = R.drawable.ic_logout
        }

        AppStatus.LOGINED.name -> {
            description = context.getString( R.string.msg_logined)
            smallIcon = R.drawable.ic_login
        }

        AppStatus.LOGOUTED.name -> {
            description = context.getString( R.string.msg_logouted)
            smallIcon = R.drawable.ic_logout
        }
    }

    val title = String.format(
        context.getString( R.string.app_status),
        context.getString( R.string.app_name)
    )

    val bigTextStyle = NotificationCompat.BigTextStyle()
        .setBigContentTitle(title)
        .bigText( description)

    val openMainScreenAction = getOpenMainScreenAction( context)

    val notificationBuilder = NotificationCompat.Builder(
        context,
        OidcService.CHANNEL_ID
    )
        .setStyle(bigTextStyle)
        .setWhen(System.currentTimeMillis())
        .setSmallIcon(smallIcon)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .addAction(openMainScreenAction)
        .setAutoCancel(true)
        .setOngoing(true)

    if ( AppStatus.LOGINED.name == appStatus ) {

        notificationBuilder.addAction( getLogoutAction( context))

    } else if ( AppStatus.LOGOUTED.name == appStatus
        || AppStatus.REGISTERED.name == appStatus
    ) {
        notificationBuilder.addAction( getLoginAction( context))
    }

    val notification = notificationBuilder.build()
    notification.flags = notification.flags or Notification.FLAG_NO_CLEAR

    return notification
}

@ExperimentalComposeUiApi
fun getLoginAction(context: Context) : NotificationCompat.Action {
    val intent = getMainActivityIntent(context = context)
    intent.putExtra(ACTION_TYPE_KEY, ACTION_TYPE_VALUE_LOGIN)

    return getNotificationAction(
        context = context,
        intent = intent,
        android.R.drawable.ic_menu_view,
        context.getString(R.string.login)
    )
}

@ExperimentalComposeUiApi
fun getLogoutAction(context: Context): NotificationCompat.Action {

    val intent = getMainActivityIntent(context = context)
    intent.putExtra( ACTION_TYPE_KEY, ACTION_TYPE_VALUE_LOGOUT)

    return getNotificationAction(
        context = context,
        intent = intent,
        android.R.drawable.ic_menu_view,
        context.getString(R.string.logout)
    )
}

@ExperimentalComposeUiApi
fun getOpenMainScreenAction(context: Context) : NotificationCompat.Action {
    val intent =getMainActivityIntent(context = context)
    intent.putExtra(ACTION_TYPE_KEY, ACTION_TYPE_VALUE_OPEN_APP)

    return getNotificationAction(
        context = context,
        intent = intent,
        android.R.drawable.ic_menu_view,
        context.getString(R.string.open_app)
    )
}

@ExperimentalComposeUiApi
private fun getMainActivityIntent( context: Context): Intent {
    val intent = Intent( context, MainActivity::class.java)
    intent.addCategory( Intent.CATEGORY_LAUNCHER)
    intent.addFlags( Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)

    return intent
}

private fun getNotificationAction(
    context: Context,
    intent: Intent,
    icon: Int,
    title: String
): NotificationCompat.Action {

    var requestCode = when(intent.getStringExtra(ACTION_TYPE_KEY)) {
        ACTION_TYPE_VALUE_OPEN_APP -> 0
        ACTION_TYPE_VALUE_LOGIN -> 1
        ACTION_TYPE_VALUE_LOGOUT -> 2
        else -> -1
    }

    val pendingIntent = PendingIntent.getActivity( context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    return NotificationCompat.Action(
        icon,
        title,
        pendingIntent
    )
}

