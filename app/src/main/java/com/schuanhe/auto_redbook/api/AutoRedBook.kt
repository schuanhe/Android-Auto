package com.schuanhe.auto_redbook.api

import android.os.Build
import android.util.Log
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import com.schuanhe.andro_auto_api.requireBaseAccessibility
import com.schuanhe.andro_auto_api.waitBaseAccessibility
import com.schuanhe.auto.core.AutoApi
import com.schuanhe.auto.core.api.back
import com.schuanhe.auto.core.api.waitForApp
import com.schuanhe.auto.core.api.withId
import com.schuanhe.auto.core.requireAutoService
import com.schuanhe.auto.core.viewfinder.SF
import com.schuanhe.auto.core.viewfinder.SG
import com.schuanhe.auto.core.viewfinder.checked
import com.schuanhe.auto.core.viewfinder.clickable
import com.schuanhe.auto.core.viewfinder.containsDesc
import com.schuanhe.auto.core.viewfinder.containsText
import com.schuanhe.auto.core.viewfinder.desc
import com.schuanhe.auto.core.viewfinder.id
import com.schuanhe.auto.core.viewfinder.text
import com.schuanhe.auto.core.viewfinder.type
import com.schuanhe.auto.core.viewnode.ViewNode
import com.schuanhe.auto_redbook.toast
import kotlinx.coroutines.delay
import timber.log.Timber

var keyInterval = 0;
@RequiresApi(Build.VERSION_CODES.N)
suspend fun actAutoRedBook(act: ComponentActivity) {

    val keyString = listOf("小红书", "小红书热榜", "小红书2")

    waitBaseAccessibility(60000)
    log("打开小红书")
    if (!openApp(act, "com.xingin.xhs"))
        return
    log("打开搜索")
    val searchElementFound = SF.desc("搜索").clickable()
    // 寻找搜索按钮
    if (!searchElementFound.globalClick()) {
        log("点击搜索按钮失败", 3)
        return
    }
    // 寻找编辑框
    if (keyString.size < keyInterval){
        log("搜索完所有关键词")
        return
    }
    searchInput(keyString)

    // 等待3s
    delay(3000)

    val list = SG(SF.where(SF.id("g86").or().id("hq")).type("TextView")).findAll()
    list.forEach {
        clickPost(it)
        delay(1000)
    }


}

// 打开应用
suspend fun openApp(act: ComponentActivity,packageName: String): Boolean {
    act.startActivity(act.packageManager.getLaunchIntentForPackage(packageName))
    return if (waitForApp(packageName, 5000)) {
        true
    }else{
        log("打开应用失败, 结束脚本", 3)
        false
    }
}

// 寻找编辑框并且输入内容
suspend fun searchInput(texts: List<String>) {
    SF.type("EditText").require(2000).apply {
        this.text = texts[keyInterval]
        keyInterval ++
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
        delay(3000)
        copyUrl()
        back()
    }
}

// 复制url
suspend fun copyUrl() {
    log("点击分享")
    if (!SF.desc("分享").tryClick()) {
        log("点击分享失败", 3)
        return
    }
    delay(1000)
    log("点击复制链接")
    if (!SF.desc("复制链接").childAt(0)?.click()!!) {
        log("点击复制链接失败", 3)
        return
    }
    log("复制链接成功")
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