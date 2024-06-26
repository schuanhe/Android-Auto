package com.schuanhe.auto_redbook.api

import android.os.Build
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import com.schuanhe.auto.core.AutoApi
import com.schuanhe.auto.core.AutoApi.Companion.back
import com.schuanhe.auto.core.api.recents
import com.schuanhe.auto.core.api.swipe
import com.schuanhe.auto.core.api.waitForApp
import com.schuanhe.auto.core.viewfinder.ConditionGroup
import com.schuanhe.auto.core.viewfinder.SF
import com.schuanhe.auto.core.viewfinder.SG
import com.schuanhe.auto.core.viewfinder.desc
import com.schuanhe.auto.core.viewfinder.id
import com.schuanhe.auto.core.viewfinder.matchText
import com.schuanhe.auto.core.viewfinder.text
import com.schuanhe.auto.core.viewfinder.type
import com.schuanhe.auto.core.viewnode.ViewNode
import com.schuanhe.auto_redbook.config.Config.Companion.APIHOST
import com.schuanhe.auto_redbook.convertLink
import com.schuanhe.auto_redbook.getClipboardText
import com.schuanhe.auto_redbook.log
import com.schuanhe.auto_redbook.okhttp
import com.schuanhe.auto_redbook.switchTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URLEncoder

/**
 * 启动指定包名的应用。
 *
 * @param act 当前活动组件。
 * @param packageName 要启动的应用的包名。
 * @return 返回是否成功启动应用。成功返回true，失败返回false。
 */
suspend fun openApp(act: ComponentActivity, packageName: String): Boolean {
    act.startActivity(act.packageManager.getLaunchIntentForPackage(packageName))
    // 等待应用启动，超时5秒
    return if (waitForApp(packageName, 5000)) {
        true
    } else {
        log("打开应用【$packageName】失败, 结束脚本", 3)
        false
    }
}

/**
 * 在搜索框中输入文本。
 *
 * @param texts 要输入的文本列表。
 */
suspend fun searchInput(texts: List<String>) {
    SF.type("EditText").require(2000).apply {
        this.text = texts[keyInterval]
        tryClick()
        delay(300)
        // 根据Android版本或服务类型发送搜索事件
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            sendImeAction()
        } else if (AutoApi.serviceType == AutoApi.SERVICE_TYPE_INSTRUMENTATION) {
            AutoApi.sendKeyCode(KeyEvent.KEYCODE_ENTER)
        }
    }
}

/**
 * 切换最新列表
 */

suspend fun switchList() {
    delay(3000)
    SF.text("全部bitmap").require(2000).apply {
        tryClick()
        delay(1000)
        SF.text("最新").require(2000).apply {
            tryClick()
        }
    }
}


/**
 * 获取列表帖子（兼容Android 24以下版本）。
 *
 */
suspend fun getListPostNoAndroid24() {
    // 寻找列表项并匹配时间格式
    val matchAll = listOf(
        "^\\d{4}-\\d{2}-\\d{2}$",
        "^\\d{2}-\\d{2}$",
        "^\\d+(天|小时|分钟)前$",
        "^(昨|今)天 \\d{2}:\\d{2}$"
    )

    try {
        var listSG: ConditionGroup = SG()
        matchAll.forEachIndexed { index, regex ->
            listSG = if (index == 0) {
                listSG.matchText(regex)
            } else {
                listSG.or().matchText(regex)
            }
        }

        listSG.require(4000)
        val list = listSG.findAll()
        log("搜索结果：${list.size}")
        // 遍历并处理每个帖子
        list.forEach {
            clickPost(it)
            delay(500)
        }
    } catch (e: Exception) {
        linkRepeat++
        log("获取列表帖子失败[$linkRepeat]:${e.message}")
        return
    }
    log("下滑")
    swipe(50, 90, 50, 5, 100)
}

/**
 * 点击帖子。
 *
 * @param it 帖子的ViewNode对象。
 */
suspend fun clickPost(it: ViewNode) {
    log("点击帖子[${it.text}]")
    if (it.tryClick()) {
        // 判断是否进入详情成功
        SF.desc("返回").require(2000).apply {
            copyUrl()
            handleUrlNoAndroid24()
        }
//        getPostContent()
    }
    // 判断当前页面
    log("当前页面: ${AutoApi.currentPage}")

    var maxBack = 0;
    while (AutoApi.currentPage == "as4.j" ||
        AutoApi.currentPage == "com.xingin.matrix.notedetail.NoteDetailActivity" ||
        AutoApi.currentPage == "com.xingin.matrix.detail.activity.DetailFeedActivity"
    ) {
        // 返回
        back()
        delay(1000)
    }

//    while (AutoApi.currentPage != "com.xingin.alioth.search.GlobalSearchActivity") {
//        // 返回
//        back()
//        delay(1000)
//    }

}

/**
 * 获取帖子内容。
 */
suspend fun getPostContent() {
    try {
        SF.id("nickNameTV").require(2000).apply {
            // 作者
            val author = SF.id("nickNameTV").text
            // 标题
            val title = SF.id("g9x").text
            // 内容
            val content = SF.id("dqo").text
            log("获取帖子内容:[作者:$author,标题:$title,内容:$content]")
        }
    } catch (e: Exception) {
        log("获取帖子内容失败", 1)
    }

}

/**
 * 复制url。
 */
suspend fun copyUrl(isOne: Boolean = true) {
    delay(1000)
    try {
        log("点击分享")
        if (!SF.desc("分享").require(2000).click()) {
            log("点击分享失败", 3)
            return
        }
//        log("点击复制链接")
        var copyLink = SF.desc("复制链接").require(3000)

        if (!copyLink.isClickable()) {
            copyLink = copyLink.childAt(0)!!
        }
        copyLink.tryClick()
    } catch (e: Exception) {
        log("点击复制链接失败", 3)
        if (isOne)
            copyUrl(false)
    }
}

/**
 * 处理复制的url。
 *
 * @param act 当前活动组件。
 */
suspend fun handleUrl(act: ComponentActivity) {
    log("开始处理复制链接")
    switchTask("自动化小红书")
    val clipboardText = getClipboardText()
    if (clipboardText != null) {
        log("读取剪切板成功:[$clipboardText]")
        val link = convertLink(clipboardText)
        log("处理链接成功:[$link]")
    } else {
        log("读取剪切板失败", 2)
    }
//    switchTask("小红书")
    openApp(act, "com.xingin.xhs")
    log("处理复制链接成功")
}

/**
 * 处理复制的url（兼容Android 24以下版本）。
 *
 */
suspend fun handleUrlNoAndroid24() {
    val clipboardText = getClipboardText()
    if (clipboardText != null) {
        log("读取剪切板成功:[$clipboardText]")
        val link = convertLink(clipboardText) ?: return
        if (!linkAuto(link)) {
            log("链接不对劲:[$link]")
            return
        }
        log("处理链接成功:[$link]")
        dataAddByKey(link)
    } else {
        log("读取剪切板失败", 2)
    }
}


/**
 * 链接转换
 */
// 过时
@Deprecated("不得行哥们")
suspend fun linkToUrl(link: String): String {
    return if (link.contains("xhslink.com")) {
        var result = okhttp(link, 0, null, mapOf("User-Agent" to "curl/7.71.1", "Accept" to "*/*"))
        log("链接转返回:[$result]")
        val pattern = "https?://www.xiaohongshu.com/discovery/item/[a-zA-Z0-9]+".toRegex()
        result = result?.let { pattern.find(it)?.value }
        result ?: link
    } else {
        link
    }
}

fun linkAuto(link: String?): Boolean {
    return !link.isNullOrEmpty() && link.contains("discovery/item")
}


/**
 * 获取关键词
 */
suspend fun getKeyword(): MutableList<Pair<Triple<String, Long, Int>, MutableList<String>>> {
    val linkAndKeyList = mutableListOf<Pair<Triple<String, Long, Int>, MutableList<String>>>()
    val result = okhttp(APIHOST + "getKeywordsCache")
    return if (result.isNullOrEmpty()) {
        log("获取关键词失败")
        linkAndKeyList
    } else {
        // 将字符串转换为json数组
        val jsonArray = JSONArray(result)
//        val keywordList = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            val keywordObject = jsonArray.getJSONObject(i)
            val keyword = keywordObject.getString("sKeyword")
            val iLastUpdateTime = keywordObject.getString("iLastUpdateTime")
            val iCount = keywordObject.getInt("iCount")
            val keyInFo = Triple(keyword, iLastUpdateTime.toLong(), iCount)
            linkAndKeyList.add(keyInFo to mutableListOf())
        }
        linkAndKeyList
    }
}

/**
 * 存入关键词
 */
suspend fun dataAddByKey(link: String) {
    // 判断链接是否重复
    if (linkAndKeyList[keyInterval].second.contains(link)) {
        log("链接重复")
        linkRepeat++
        return
    }

    // 判断链接是否超时
    val time = getLinkTime(link)

    log("链接时间:$time")

    if (time < linkAndKeyList[keyInterval].first.second) {
        log("链接超时")
        linkTimeout = true
        return
    }

    if (linkAndKeyList[keyInterval].second.size >= linkAndKeyList[keyInterval].first.third){
        log("链接已满:需要\\实际：${linkAndKeyList[keyInterval].first.third}\\${linkAndKeyList[keyInterval].second.size}")
        return
    }


    linkRepeat = 0

    val encodedUrl = withContext(Dispatchers.IO) {
        URLEncoder.encode(linkAndKeyList[keyInterval].first.first, "UTF-8")
    }

    val result = okhttp(APIHOST + "dataAddByKey?link=$link&keyword=${encodedUrl}")
    if (!result.isNullOrEmpty() && result == "true") {
        log("存入成功[${linkAndKeyList[keyInterval].first}]:$link")
        linkAndKeyList[keyInterval].second.add(link)
    } else {
        log("存入失败:$result")
    }
}

/**
 * 清楚后台
 */

suspend fun clearBackground() {
    try {
        recents()
        SF.desc("移除小红书。").and().id("dismiss_task").require(2000).tryClick()
    } catch (e: Exception) {
        log("移除小红书失败", 2)
    }


    delay(1000)
    // 点击自动化

    try {
        SF.desc("自动化小红书").and().text("自动化小红书").require(2000).tryClick()
    } catch (e: Exception) {
        log("点击自动化小红书失败", 2)
    }

}

/**
 * 小红书提取链接时间
 */

fun getLinkTime(link: String): Long {
    val pattern = "https?://www.xiaohongshu.com/discovery/item/([a-zA-Z0-9]{8})\\S+".toRegex()

    val matchResult = pattern.find(link)
    if (matchResult != null) {
        val itemID = matchResult.groupValues[1]
        // 获取时间戳并转换为Long类型
        return itemID.toLong(16)
    }
    log("链接格式错误2")

    return 0L
}


