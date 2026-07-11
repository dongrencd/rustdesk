package com.carriez.flutter_hbb

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

const val TERMINAL_KEEP_ALIVE_NOTIFY_ID = 3
const val TERMINAL_KEEP_ALIVE_CHANNEL_ID = "RustDeskTerminal"

class TerminalKeepAliveService : Service() {
    private val logTag = "TerminalKeepAlive"
    private val powerManager: PowerManager by lazy {
        applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
    }
    private val wakeLock: PowerManager.WakeLock by lazy {
        powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "rustdesk:terminal_keep_alive"
        )
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(logTag, "Terminal keep-alive service created")
        startForeground(TERMINAL_KEEP_ALIVE_NOTIFY_ID, createNotification())
        if (!wakeLock.isHeld) {
            // Keep the CPU available for the active terminal socket while the
            // app is backgrounded. This does not wake or keep the display on.
            wakeLock.acquire()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(logTag, "Terminal keep-alive service started: $startId")
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        Log.d(logTag, "Terminal keep-alive service destroyed")
        if (wakeLock.isHeld) {
            wakeLock.release()
        }
        super.onDestroy()
    }

    private fun createNotification(): Notification {
        val channelId = createNotificationChannel()
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(this, 0, intent, FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, intent, FLAG_UPDATE_CURRENT)
        }
        return NotificationCompat.Builder(this, channelId)
            .setOngoing(true)
            .setSmallIcon(R.mipmap.ic_stat_logo)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentTitle(DEFAULT_NOTIFY_TITLE)
            .setContentText("${translate("Terminal")}: ${translate(DEFAULT_NOTIFY_TEXT)}")
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setColor(ContextCompat.getColor(this, R.color.primary))
            .setWhen(System.currentTimeMillis())
            .build()
    }

    private fun createNotificationChannel(): String {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return ""
        }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            TERMINAL_KEEP_ALIVE_CHANNEL_ID,
            "RustDesk Terminal",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "RustDesk terminal keep-alive channel"
            lightColor = Color.BLUE
            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }
        manager.createNotificationChannel(channel)
        return TERMINAL_KEEP_ALIVE_CHANNEL_ID
    }
}
