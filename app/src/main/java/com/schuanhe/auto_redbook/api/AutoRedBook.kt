package com.schuanhe.auto_redbook.api

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import com.schuanhe.andro_auto_api.waitBaseAccessibility
import com.schuanhe.auto.core.api.setScreenSize
import com.schuanhe.auto.core.viewfinder.SF
import com.schuanhe.auto.core.viewfinder.clickable
import com.schuanhe.auto.core.viewfinder.desc
import com.schuanhe.auto_redbook.log
import com.schuanhe.auto_redbook.openScheme
import com.schuanhe.auto_redbook.scheme.RedBook

var keyInterval = 0
@RequiresApi(Build.VERSION_CODES.N)
suspend fun actAutoRedBook(act: ComponentActivity) {
    setScreenSize(100, 100)
    val keyString = listOf("自动化", "关键词2", "小红书2")

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

    getListPost(act)

    log("第二列")
    getListPost(act)
}
var linkList = mutableListOf<String>()
suspend fun actAutoRedBookNoAndroid24(act: ComponentActivity) {
    setScreenSize(100, 100)
    val keyString = listOf("自动化", "关键词2", "小红书2")
    waitBaseAccessibility(60000)

    log("使用Scheme搜索关键词: ${keyString[keyInterval]}")

    openScheme(RedBook.xhsSearchWithKeyword(keyString[keyInterval]))

    while (linkList.size <= 10){
        getListPostNoAndroid24(act)
    }
    log("搜索完所有关键词:${linkList}")




//
//    log("第二列")
//    getListPostNoAndroid24(act)
}

// 打开应用