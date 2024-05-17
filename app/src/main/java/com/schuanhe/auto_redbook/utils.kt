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
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * # utils
 *
 * Created on 2020/6/11
 * @author Vove
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
