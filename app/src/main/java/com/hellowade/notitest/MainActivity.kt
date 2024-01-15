package com.hellowade.notitest

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class MainActivity : AppCompatActivity() {

    private val iconImageView: ImageView by lazy {
        findViewById(R.id.iconImageView)
    }

    private val packageNameTextView: TextView by lazy {
        findViewById(R.id.packageNameTextView)
    }

    private val titleTextView: TextView by lazy {
        findViewById(R.id.titleTextView)
    }

    private val messageTextView: TextView by lazy {
        findViewById(R.id.messageTextView)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!isNotificationPermissionGranted()) {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }
    }

    private fun isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.isNotificationListenerAccessGranted(
                ComponentName(
                    application,
                    NotiListenerService::class.java,
                )
            )
        } else {
            NotificationManagerCompat.getEnabledListenerPackages(applicationContext)
                .contains(applicationContext.packageName)
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver, IntentFilter("noti-event")
        );
    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onReceive(context: Context, intent: Intent) {
            val icon = intent.getParcelableExtra("icon", Icon::class.java)
            iconImageView.setImageIcon(icon)
            val packageName = intent.getStringExtra("packageName")
            packageNameTextView.text = packageName
            packageName?.let {
                val appIcon = packageManager.getApplicationIcon(it);
                iconImageView.setImageDrawable(appIcon)
            }
            val title = intent.getStringExtra("title")
            titleTextView.text = title
            val message = intent.getStringExtra("message")
            messageTextView.text = message
        }
    }
}