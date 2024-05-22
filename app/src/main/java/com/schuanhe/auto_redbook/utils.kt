package com.schuanhe.auto_redbook

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.schuanhe.auto.core.api.recents
import com.schuanhe.auto.core.api.waitForApp
import com.schuanhe.auto.core.viewfinder.SF
import com.schuanhe.auto.core.viewfinder.desc
import com.schuanhe.auto.core.viewfinder.type
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * # utils
 *
 * Created on 2024/5/22
 * @author schuanhe
 */


fun launchWithExpHandler(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
) = GlobalScope.launch(context + ExceptionHandler, start, block)


val ExceptionHandler by lazy {
    CoroutineExceptionHandler { _, throwable ->
        toast("执行失败： ${throwable.message ?: "$throwable"}")
        throwable.printStackTrace()
    }
}

val mainHandler by lazy {
    Handler(Looper.getMainLooper())
}

fun runOnUi(block: () -> Unit) {
    if (Looper.getMainLooper() == Looper.myLooper()) {
        block()
    } else {
        mainHandler.post(block)
    }
}


fun toast(m: String) =
    runOnUi {
        Toast.makeText(DemoApp.INS, m, Toast.LENGTH_SHORT).show()
    }


/**
 * 切换到指定的应用任务。
 *
 * @param appName 要切换到的应用名称。
 */
suspend fun switchTask(appName: String){
    log("切换任务[$appName]")
    recents()
    if (!SF.desc("$appName,未加锁").and().type("FrameLayout").require(2000).tryClick()) {
        log("任务[${appName}]没有开启", 2)
    }
    delay(300)
}

/**
 * 切换到指定的应用任务，并检查是否成功切换。
 *
 * @param appName 要切换到的应用名称。
 * @param packageName 要切换到的应用的包名。
 * @return 返回是否成功切换到指定应用。
 */
suspend fun switchTask(appName: String, packageName: String): Boolean {
    log("切换任务[$appName]")
    recents()
    if (!SF.desc("$appName,未加锁").and().type("FrameLayout").require(2000).tryClick()) {
        log("任务[${appName}]没有开启", 2)
    }
    delay(300)
    return if (waitForApp(packageName, 5000)) {
        true
    } else {
        log("切换任务[${appName}]失败", 2)
        false
    }
}


/**
 * 从剪贴板获取文本。
 *
 * @return 返回剪贴板中的文本内容。
 */
suspend fun getClipboardText(): String? {
    return withContext(Dispatchers.Main) {
        val clipboardManager =
            DemoApp.INS.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboardManager?.primaryClip?.getItemAt(0)?.text?.toString()
    }
}

/**
 * 将获取的链接进行转换处理。
 *
 * @param urlText 原始链接文本。
 * @return 返回处理后的链接。
 */
fun convertLink(urlText: String): String? {
//    val pattern = "https?://[a-z.]+/[a-zA-Z0-9]+/item/[a-zA-Z0-9]+".toRegex()
    val pattern = "https?://[a-zA-Z./0-9]+".toRegex()
    val matcher = pattern.find(urlText)
    if (matcher == null) {
        log("链接格式错误", 2)
        return null
    }

    return matcher.value
}

/**
 * 打开指定的URLScheme。
 *
 * @param url 要打开的URL链接。
 */
fun openScheme(url:String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(url)
    }
    if (intent.resolveActivity(DemoApp.INS.packageManager) != null) {
        // 启动对应的Activity
        log("打开Scheme[$url]")
        DemoApp.INS.startActivity(intent)
    } else {
        log("没有应用可以处理该Scheme",3)
    }
}

/**
 * 记录日志信息。
 *
 * @param msg 要记录的日志信息。
 * @param int 日志级别。
 */
fun log(msg: String, int: Int = 0) {
    when (int) {
        0 -> Timber.tag("AutoRedBook").d(msg)
        1 -> Timber.tag("AutoRedBook").i(msg)
        2 -> Timber.tag("AutoRedBook").w(msg)
        3 -> Timber.tag("AutoRedBook").e(msg)
        else -> Timber.tag("AutoRedBook").d(msg)
    }
}

/**
 * 发起HTTP请求。
 *
 * @param url 请求的URL地址。
 * @param mode 请求模式，0表示GET请求，1表示POST请求。
 * @param data 请求的数据，仅在POST请求模式下生效。
 */
suspend fun okhttp(url: String, mode: Int = 0, data: String? = null, headers: Map<String, String>? = null): String? {
    val response = suspendCancellableCoroutine<Response> { continuation ->
        // 构建请求
        val requestBuilder = Request.Builder().url(url)

        // 根据headers参数设置请求头
        headers?.forEach { (key, value) ->
            requestBuilder.addHeader(key, value)
        }

        // 根据mode设置请求类型和请求体
        if (mode == 1 && data != null) {
            val requestBody =
                data.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            requestBuilder.post(requestBody)
        } else {
            requestBuilder.get()
        }

        // 构建请求对象
        val request = requestBuilder.build()

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
        return response.body?.string()
    } else {
        log("请求失败，响应码：${response.code}")
    }
    return null
}