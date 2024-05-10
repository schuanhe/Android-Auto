package com.schuanhe.auto_redbook.api

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import com.schuanhe.andro_auto_api.requireBaseAccessibility
import com.schuanhe.andro_auto_api.waitBaseAccessibility
import com.schuanhe.auto.core.AutoApi
import com.schuanhe.auto.core.api.back
import com.schuanhe.auto.core.api.recents
import com.schuanhe.auto.core.api.scrollUp
import com.schuanhe.auto.core.api.waitForApp
import com.schuanhe.auto.core.api.withId
import com.schuanhe.auto.core.requireAutoService
import com.schuanhe.auto.core.viewfinder.ConditionGroup
import com.schuanhe.auto.core.viewfinder.SF
import com.schuanhe.auto.core.viewfinder.SG
import com.schuanhe.auto.core.viewfinder.checked
import com.schuanhe.auto.core.viewfinder.clickable
import com.schuanhe.auto.core.viewfinder.containsDesc
import com.schuanhe.auto.core.viewfinder.containsText
import com.schuanhe.auto.core.viewfinder.desc
import com.schuanhe.auto.core.viewfinder.id
import com.schuanhe.auto.core.viewfinder.matchText
import com.schuanhe.auto.core.viewfinder.text
import com.schuanhe.auto.core.viewfinder.type
import com.schuanhe.auto.core.viewnode.ViewNode
import com.schuanhe.auto_redbook.actions.Action
import com.schuanhe.auto_redbook.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber

var keyInterval = 0;
@RequiresApi(Build.VERSION_CODES.N)
suspend fun actAutoRedBook(act: ComponentActivity) {

    val keyString = listOf("关键词", "关键词2", "小红书2")

    waitBaseAccessibility(60000)
    log("打开小红书")
    if (!openApp(act, "com.xingin.xhs"))
        return
    log("打开搜索")
    val searchElementFound = SF.desc("搜索").clickable().require(2000)
    // 寻找搜索按钮
    if (!searchElementFound.globalClick()) {
        log("点击搜索按钮失败", 3)
        return
    }
    // 寻找编辑框
//    if (keyString.size < keyInterval){
//        log("搜索完所有关键词")
//        return
//    }
    searchInput(keyString)

    delay(3000)

    val matchAll = listOf("^\\d{4}-\\d{2}-\\d{2}$",
        "^\\d{2}-\\d{2}$",
        "^\\d+天前$",
        "^(昨|今)天 \\d{2}:\\d{2}$")
    var listSG: ConditionGroup = SG()

    matchAll.forEachIndexed { index, regex ->
        listSG = if (index == 0) {
            listSG.matchText(regex)
        } else {
            listSG.or().matchText(regex)
        }
    }

    val list = listSG.findAll()
    log("搜索结果：${list.size}")
    list.forEach {
        log("开始处理[${it.text}]")
        clickPost(it)
        delay(1000)
        // 处理链接
        handleUrl(act)
        delay(1000)
        log("处理结束[${it.text}]")
    }

    // 下滑一手
    scrollUp()


}

// 打开应用
suspend fun openApp(act: ComponentActivity,packageName: String): Boolean {
    act.startActivity(act.packageManager.getLaunchIntentForPackage(packageName))
    return if (waitForApp(packageName, 5000)) {
        true
    }else{
        log("打开应用【$packageName】失败, 结束脚本", 3)
        false
    }
}

// 寻找编辑框并且输入内容
suspend fun searchInput(texts: List<String>) {
    SF.type("EditText").require(2000).apply {
        this.text = texts[keyInterval]
//        keyInterval ++
        tryClick()
        delay(300)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            sendImeAction()
        } else if (AutoApi.serviceType == AutoApi.SERVICE_TYPE_INSTRUMENTATION) {
            AutoApi.sendKeyCode(KeyEvent.KEYCODE_ENTER)
        }

    }
}

// 点击帖子
suspend fun clickPost(it: ViewNode) {
    log("点击帖子[${it.text}]")
    if(it.tryClick()){
        getPostContent()
        copyUrl()
        back()
    }
}

// 获取帖子内容
suspend fun getPostContent(){
    // 作者
    val author = SF.id("nickNameTV").require(2000).text
    // 标题
    val title = SF.id("g9x").require(2000).text
    // 内容
    val content = SF.id("dqo").require(2000).text

    log("获取帖子内容:[作者:$author,标题:$title,内容:$content]")
}

// 复制url
suspend fun copyUrl() {
    log("点击分享")
    if (!SF.desc("分享").require(2000).tryClick()) {
        log("点击分享失败", 3)
        return
    }
    log("点击复制链接")
    if (!SF.desc("复制链接").require(2000).childAt(0)?.click()!!) {
        log("点击复制链接失败", 3)
        return
    }
    log("复制链接成功")
}

// 处理复制的url
suspend fun handleUrl(act: ComponentActivity) {
    log("开始处理复制链接")
    switchTask("自动化小红书")
    val clipboardText = getClipboardText(act)
    if (clipboardText != null) {
        log("读取剪切板成功:[$clipboardText]")
        val link = convertLink(clipboardText)
        log("处理链接成功:[$link]")
    } else {
        log("读取剪切板失败", 2)
    }
    switchTask("小红书")
    log("处理复制链接成功")
}

suspend fun switchTask(appName: String){
    log("切换任务[$appName]")
    recents()
    if (!SF.desc("$appName,未加锁").and().type("FrameLayout").require(2000).tryClick()) {
        log("任务[${appName}]没有开启", 2)
    }
    delay(300)
}
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


private suspend fun getClipboardText(context: Context): String? {
    return withContext(Dispatchers.Main) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboardManager?.primaryClip?.getItemAt(0)?.text?.toString()
    }
}

private fun convertLink(urlText: String): String? {
    val pattern = "https?://[a-z.]+/[a-zA-Z0-9]+".toRegex()
    val matcher = pattern.find(urlText)
    if (matcher == null) {
        log("链接格式错误", 2)
        return null
    }

    return matcher.value
}

fun log(msg: String, int: Int = 0) {
    when (int) {
        0 -> Timber.tag("AutoRedBook").d(msg)
        1 -> Timber.tag("AutoRedBook").i(msg)
        2 -> Timber.tag("AutoRedBook").w(msg)
        3 -> Timber.tag("AutoRedBook").e(msg)
        else -> Timber.tag("AutoRedBook").d(msg)
    }
}