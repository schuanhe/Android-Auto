package com.schuanhe.auto_redbook.api

import com.schuanhe.andro_auto_api.waitBaseAccessibility
import com.schuanhe.auto.core.api.setScreenSize
import com.schuanhe.auto_redbook.log
import com.schuanhe.auto_redbook.openScheme
import com.schuanhe.auto_redbook.scheme.RedBook
import kotlinx.coroutines.delay

var keyInterval = 0
var linkAndKeyList = mutableListOf<Pair<String, MutableList<String>>>()
var linkRepeat = 0
suspend fun actAutoRedBookNoAndroid24() {
    setScreenSize(100, 100)

    try {
        if (linkAndKeyList.isEmpty()){
            linkAndKeyList = getKeyword()
        }
    }catch (e: Exception){
        log("获取关键词失败", 3)
    }


    waitBaseAccessibility(60000)

    try {
        linkAndKeyList.forEach { _ ->
            log("使用Scheme搜索关键词: ${linkAndKeyList[keyInterval].first}")
            openScheme(RedBook.xhsSearchWithKeyword(linkAndKeyList[keyInterval].first))

            try {
                switchList()
            }catch (e: Exception){
                log("切换列表失败", 3)
                keyInterval ++
            }

//        delay(2000)
            delay(3000)
            while (linkRepeat < 4 && linkAndKeyList[keyInterval].second.size < 100) {
                getListPostNoAndroid24()
            }

            log("关键词搜索完成【${linkAndKeyList[keyInterval].first}】【${linkAndKeyList[keyInterval].second.size}】:${linkAndKeyList[keyInterval].second}")
            keyInterval ++
            linkRepeat = 0
        }
    }catch (e: Exception){
        log("搜索关键词失败![{$e}]", 3)
    }


    log("搜索完所有关键词:[$linkAndKeyList]")

    // 清除后台
    clearBackground()
    linkRepeat = 0
    linkAndKeyList = mutableListOf()
    keyInterval = 0

}

