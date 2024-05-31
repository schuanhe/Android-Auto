package com.schuanhe.auto_redbook.api

import com.schuanhe.andro_auto_api.waitBaseAccessibility
import com.schuanhe.auto.core.api.setScreenSize
import com.schuanhe.auto_redbook.FileLoggingTree
import com.schuanhe.auto_redbook.log
import com.schuanhe.auto_redbook.openScheme
import com.schuanhe.auto_redbook.scheme.RedBook
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.URLEncoder

var keyInterval = 0
var linkAndKeyList = mutableListOf<Pair<String, MutableList<String>>>()
var linkRepeat = 0
suspend fun actAutoRedBookNoAndroid24() {
    setScreenSize(100, 100)

    try {
        Timber.uprootAll() // 移除所有已添加的日志树
        Timber.plant(FileLoggingTree()) // 添加新的日志树
        if (linkAndKeyList.isEmpty()) {
            linkAndKeyList = getKeyword()
        }
    } catch (e: Exception) {
        log("获取关键词失败", 3)
    }


    waitBaseAccessibility(60000)

    try {
        linkAndKeyList.forEach { _ ->
            try {
                log("使用Scheme搜索关键词: ${linkAndKeyList[keyInterval].first}")


                val encodedUrl = withContext(Dispatchers.IO) {
                    URLEncoder.encode(linkAndKeyList[keyInterval].first, "UTF-8")
                }

                openScheme(RedBook.xhsSearchWithKeyword(encodedUrl))

                try {
                    switchList()
                } catch (e: Exception) {
                    log("切换列表失败", 3)
                }

//        delay(2000)
                delay(3000)
                while (linkRepeat < 4 && linkAndKeyList[keyInterval].second.size < 100) {
                    getListPostNoAndroid24()
                }
            } catch (e: Exception) {
                log("关键词搜索失败![{$e}]", 3)
            } finally {
                log("关键词搜索完成【${linkAndKeyList[keyInterval].first}】【${linkAndKeyList[keyInterval].second.size}】:${linkAndKeyList[keyInterval].second}")
                keyInterval++
                linkRepeat = 0
            }
        }
    } catch (e: Exception) {
        log("搜索关键词列表失败![{$e}]", 3)
    }


    log("搜索完所有关键词:[$linkAndKeyList]")

    // 清除后台
    clearBackground()
    linkRepeat = 0
    linkAndKeyList = mutableListOf()
    keyInterval = 0


}

