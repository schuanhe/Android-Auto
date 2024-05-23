package com.schuanhe.auto_redbook.actions

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.schuanhe.auto.core.api.back
import com.schuanhe.auto.core.api.home
import com.schuanhe.auto.core.api.powerDialog
import com.schuanhe.auto.core.api.pullNotificationBar
import com.schuanhe.auto.core.api.quickSettings
import com.schuanhe.auto.core.api.recents
import com.schuanhe.auto.core.requireAutoService
import com.schuanhe.auto_redbook.DemoApp
import com.schuanhe.auto_redbook.MainActivity
import com.schuanhe.auto_redbook.api.actAutoRedBookNoAndroid24
import com.schuanhe.auto_redbook.api.getKeyword
import com.schuanhe.auto_redbook.api.showNotification
import com.schuanhe.auto_redbook.log
import com.schuanhe.auto_redbook.toast
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * # actions
 *
 * Created on 2024/5/22
 * @author schuanhe
 */

class BaseNavigatorAction : Action() {
    override val name: String get() = "基础导航"

    override suspend fun run(act: ComponentActivity) {
        requireAutoService()

        // 请求通知权限
        requestNotificationPermission(act)

        toast("下拉通知栏..")
        pullNotificationBar()
        delay(1000)
        toast("快捷设置..")
        delay(1000)
        quickSettings()
        delay(1000)
        back()
        delay(500)
        back()
        delay(1000)
        powerDialog()
        delay(500)
        back()
        delay(1000)
        recents()
        delay(1000)
        back()
        delay(1000)
        home()
        delay(100)
        DemoApp.INS.startActivity(Intent(DemoApp.INS, MainActivity::class.java).also {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    private fun requestNotificationPermission(act: ComponentActivity) {
        val permission = "${act.packageName}.permission.NOTIFICATION"
        if (ContextCompat.checkSelfPermission(act, permission)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // 如果权限尚未被授予，则向用户请求权限
            ActivityCompat.requestPermissions(
                act,
                arrayOf(permission),
                REQUEST_NOTIFICATION_PERMISSION
            )
        }
    }

    companion object {
        private const val REQUEST_NOTIFICATION_PERMISSION = 1001
    }
}

class AutoRedBookNoAndroid24 : Action() {
    override val name: String
        get() = "小红书自动化 2(低于Android 24)"
    @RequiresApi(Build.VERSION_CODES.N)
    override suspend fun run(act: ComponentActivity) {
        actAutoRedBookNoAndroid24()
    }
}


class OKHttp : Action() {
    override val name: String
        get() = "使用okhttp完成网络请求"

    override suspend fun run(act: ComponentActivity) {
        // 使用协程来处理网络请求
        val response = suspendCancellableCoroutine<Response> { continuation ->
            // 构建请求

            val request = Request.Builder()
                .url("http://xhslink.com/j5rdJJ") // 设置请求的URL
//                .addHeader("User-Agent", "curl/7.71.1")
                .headers(Headers.headersOf(
                    "User-Agent", "curl/7.71.1"
                ))
                .build()

            log("请求头：${request.headers}")
//            request.headers

            // 发送异步请求
            val call = OkHttpClient().newCall(request)
            call.enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    continuation.resume(response) // 在协程中恢复并传递响应结果
                }

                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e) // 在协程中恢复并传递异常
                }
            })
        }

        // 处理响应结果
        if (response.isSuccessful) {
            val responseBody = response.body?.string()
            log("请求成功，响应码：${response.code}")
            log("响应体：${responseBody}")
        }
    }

}

class ShowNo : Action() {
    override val name: String
        get() = "显示通知"

    override suspend fun run(act: ComponentActivity) {
        showNotification(act)
    }

}

class PickScreenText : Action() {
    override val name: String
        get() = "获取关键词"

    override suspend fun run(act: ComponentActivity) {
        val keyword = getKeyword()

        if (keyword.isNotEmpty()) {
            toast("关键词：$keyword")
        } else {
            toast("关键词为空")
        }
    }
}
