package com.schuanhe.auto_redbook

import android.app.Application
import android.content.Intent
import android.os.Build
import com.schuanhe.andro_auto_api.AccessibilityApi
import com.schuanhe.auto.core.AppScope
import com.schuanhe.auto_redbook.service.AppAccessibilityService
import com.schuanhe.auto_redbook.service.ForegroundService
import timber.log.Timber

/**
 * # DemoApp
 *
 *
 * Created on 2024/5/22
 *
 * @author schuanhe
 */
class DemoApp : Application() {

    companion object {
        lateinit var INS: Application  // app
        lateinit var PageUpdate: AppScope
    }

    override fun onCreate() {
        INS = this

        super.onCreate()

        Timber.plant(FileLoggingTree())
        if (Timber.treeCount == 0) {
            Timber.plant(Timber.DebugTree())
        }

        AccessibilityApi.init(this,
            AppAccessibilityService::class.java
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, ForegroundService::class.java))
        } else {
            startService(Intent(this, ForegroundService::class.java))
        }
        Timber.i("DemoApp create.")
    }
}