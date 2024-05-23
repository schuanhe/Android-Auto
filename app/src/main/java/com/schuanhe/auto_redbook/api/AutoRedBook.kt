package com.schuanhe.auto_redbook.api

import com.schuanhe.andro_auto_api.waitBaseAccessibility
import com.schuanhe.auto.core.api.setScreenSize
import com.schuanhe.auto_redbook.log
import com.schuanhe.auto_redbook.openScheme
import com.schuanhe.auto_redbook.scheme.RedBook

var keyInterval = 0
var linkAndKeyList = mutableListOf<Pair<String, MutableList<String>>>()
var linkRepeat = 0
suspend fun actAutoRedBookNoAndroid24() {
    setScreenSize(100, 100)

    if (linkAndKeyList.isEmpty()){
        linkAndKeyList = getKeyword()
    }

    waitBaseAccessibility(60000)

    linkAndKeyList.forEach {
        log("使用Scheme搜索关键词: ${linkAndKeyList[keyInterval].first}")
        openScheme(RedBook.xhsSearchWithKeyword(linkAndKeyList[keyInterval].first))
        while (linkRepeat < 4 && linkAndKeyList[keyInterval].second.size < 100) {
            getListPostNoAndroid24()
        }
        log("关键词搜索完成【${linkAndKeyList[keyInterval].first}】【${linkAndKeyList[keyInterval].second.size}】:${linkAndKeyList[keyInterval].second}")
        keyInterval ++
    }

    log("搜索完所有关键词:[$linkAndKeyList]")


}

// 打开应用