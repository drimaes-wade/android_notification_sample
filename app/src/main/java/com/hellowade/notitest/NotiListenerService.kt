package com.hellowade.notitest

import android.app.Notification
import android.app.RemoteInput
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.Locale


class NotiListenerService : NotificationListenerService() {

    private val TAG = "MyNotificationListenerService"

    private val REPLY_KEYWORDS = arrayOf("reply_message", "android.intent.extra.text")

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        Log.d(TAG, "SBN INFO ${sbn?.id} ${sbn?.tag} ${sbn?.groupKey}")


        val packageName: String = sbn?.packageName ?: "Null"
        val notification: Notification? = sbn?.notification
        val extras = sbn?.notification?.extras

        val extraTitle: String = extras?.get(Notification.EXTRA_TITLE).toString()
        val extraText: String = extras?.get(Notification.EXTRA_TEXT).toString()
        val largeIcon = extras?.get(Notification.EXTRA_LARGE_ICON)
        sendMessage(packageName, largeIcon, extraTitle, extraText)

        var notiAction: Notification.Action? = null
        notification?.actions?.forEach {
            if (it.remoteInputs != null) {
                it.remoteInputs.forEach { remoteInputs ->
                    if (remoteInputs.resultKey != null && isKnownReplyKey(remoteInputs.resultKey)) {
                        notiAction = it
                    }
                }
            }
        }

        Log.d(
            TAG, "onNotificationPosted:\n" +
                    "PackageName: $packageName\n" +
                    "Title: $extraTitle\n" +
                    "Text: $extraText\n" +
                    "Icon: $largeIcon\n" +
                    "Action: $notiAction\n"
        )

        if (packageName in "com.kakao.talk" && extraTitle == "정우영1") {
            NotificationReplier(applicationContext, notiAction!!).reply(extraText)
        }


    }

    private fun isKnownReplyKey(resultKey: String): Boolean {
        if (TextUtils.isEmpty(resultKey)) return false
        val lowData = resultKey.lowercase(Locale.getDefault())
        for (keyword in REPLY_KEYWORDS) if (lowData.contains(keyword)) return true
        return false
    }

    private fun sendMessage(packageName: String, icon: Any?, title: String, message: String) {
        Log.d("messageService", "Broadcasting message")
        val intent = Intent("noti-event")
        intent.putExtra("packageName", packageName)
        if (icon != null) {
            intent.putExtra("icon", icon as Icon)
        }
        intent.putExtra("title", title)
        intent.putExtra("message", message)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    class NotificationReplier(
        val context: android.content.Context,
        val session: Notification.Action
    ) {
        fun reply(value: String) {
            val sendIntent = Intent()
            val msg = Bundle()

            session.remoteInputs.forEach { remoteInput ->
                msg.putCharSequence(remoteInput.resultKey, value)
            }
            RemoteInput.addResultsToIntent(session.remoteInputs, sendIntent, msg)
            Log.d("TAG", "Reply Sended $msg")
            try {
                session.actionIntent.send(context, 0, sendIntent)
            } catch (e: Exception) {
                Log.e("Exception", e.printStackTrace().toString())
            }
        }
    }
}