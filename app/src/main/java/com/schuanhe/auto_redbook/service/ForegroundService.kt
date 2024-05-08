package com.schuanhe.auto_redbook.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.schuanhe.andro_auto_api.requireBaseAccessibility
import com.schuanhe.auto.core.api.back
import com.schuanhe.auto.core.api.printLayoutInfo
import com.schuanhe.auto_redbook.R
import com.schuanhe.auto_redbook.api.redBookGo
import com.schuanhe.auto_redbook.launchWithExpHandler
import com.schuanhe.auto_redbook.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * # ForegroundService
 *
 * Created on 2020/6/11
 * @author Vove
 */
class ForegroundService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    private val channelId by lazy {
        val id = "ForegroundService"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val c = NotificationChannel(
                id,
                getString(R.string.fore_service),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
                enableVibration(false)
                enableLights(false)
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(c)
        }
        id
    }

    private fun getNotification() = NotificationCompat.Builder(this, channelId).apply {
        setContentTitle(getString(R.string.fore_service))
        setContentText("输出布局 on logcat")
        val printIntent = Intent(this@ForegroundService, ForegroundService::class.java)
        printIntent.action = ACTION_PRINT_LAYOUT
        val pi = PendingIntent.getService(this@ForegroundService, 0, printIntent, PendingIntent.FLAG_MUTABLE)

        // 新按钮
        val redBookGoIntent =  Intent(this@ForegroundService, ForegroundService::class.java)
        redBookGoIntent.action = ACTION_RED_BOOK_GO
        val redBookGoPendingIntent = PendingIntent.getService(this@ForegroundService, 0, redBookGoIntent, PendingIntent.FLAG_MUTABLE)


        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        setSmallIcon(R.mipmap.ic_launcher_round)
        val acb = NotificationCompat.Action.Builder(0, "输出布局", pi)
        val redBook = NotificationCompat.Action.Builder(0, "RedBookGo", redBookGoPendingIntent)
        addAction(acb.build())
        addAction(redBook.build())
        setOngoing(true)
    }.build()

    override fun onCreate() {
        super.onCreate()
        startForeground(1999, getNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.action?.also {
            parseAction(it)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun parseAction(action: String) {
        when (action) {
            ACTION_PRINT_LAYOUT -> {
                launchWithExpHandler {
                    back()
                    delay(1000)
                    printLayoutInfo()
                }
            }
            ACTION_RED_BOOK_GO -> {
                launchWithExpHandler {
                    back()
                    delay(1000)
                    redBookGo()
                }
        }
        }

    }

    companion object {
        const val ACTION_PRINT_LAYOUT = "print_layout"
        const val ACTION_RED_BOOK_GO = "red_book_go"
    }
}