package com.schuanhe.auto_redbook.api

import android.content.res.Resources
import android.graphics.Path
import com.schuanhe.andro_auto_api.AccessibilityApi
import com.schuanhe.andro_auto_api.requireBaseAccessibility
import com.schuanhe.auto.core.api.gesture
import com.schuanhe.auto.core.viewnode.ViewNode
import kotlinx.coroutines.delay

suspend fun redBookGo(includeInvisible: Boolean = true) {
    requireBaseAccessibility()
    search("搜索")
    delay(1000)
    input("测试关键词")
    search2()
    delay(3000)
    val screenWidth = Resources.getSystem().displayMetrics.widthPixels
    val screenHeight = Resources.getSystem().displayMetrics.heightPixels

    // 创建从屏幕中心到屏幕中心的路径
    val path = Path()
    path.moveTo(screenWidth / 2f - 20, screenHeight / 2f)
    path.lineTo(screenWidth / 2f - 20, screenHeight / 2f)

    print("开始点击屏幕中心")
    // 模拟点击
    gesture(duration = 100L, path = path)
}

// 点击搜索
private fun search(keyword: String) {
    val ts = ViewNode.getRoot().findDesc(0, 0, keyword)
    ts?.click()
}
// 输入关键词
private fun input(keyword: String) {
    val ts = ViewNode.getRoot().findText(0, 0, "", "android.widget.EditText")
    if (ts != null) {
        ts.focus()
        ts.text = keyword
    }else{
        println("没有找到")
    }
}
// 点击搜索
private fun search2() {
    val ts5 = ViewNode.getRoot().findText(0, 0, "搜索","android.widget.Button")
    ts5?.click()
}


// 递归搜索Desc
private fun ViewNode.findDesc(
    index: Int,
    dep: Int,
    descKey: String
): ViewNode? {
    if (isVisibleToUser) {
        if (desc()?.contains(descKey) == true){
            println("找到 $descKey")
            return this // 找到目标时返回当前的 ViewNode
        }
    }
    children.forEachIndexed { i, it ->
        val foundNode = it?.findDesc(i, dep + 1, descKey)
        if (foundNode != null) {
            return foundNode // 如果在子节点中找到了目标，立即返回该节点
        }
    }
    return null // 如果当前节点及其子节点都不包含目标，则返回 null
}


// 递归搜索Text - EditText
private fun ViewNode.findText(
    index: Int,
    dep: Int,
    textKey: String,
    byClassName : String
): ViewNode? {
    if (isVisibleToUser) {
        if (text?.contains(textKey) == true || textKey == ""){
            if (byClassName != ""){
                if (className == byClassName)
                    return this
            }else{
                return this
            }
        }
    }
    children.forEachIndexed { i, it ->
        val foundNode = it?.findText(i, dep + 1, textKey, byClassName)
        if (foundNode != null) {
            return foundNode // 如果在子节点中找到了目标，立即返回该节点
        }
    }
    return null // 如果当前节点及其子节点都不包含目标，则返回 null
}

// 递归搜索Text - EditText
private fun ViewNode.findTextEditText(
    index: Int,
    dep: Int,
): ViewNode? {
    if (isVisibleToUser) {
        if (className == "android.widget.EditText")
            return this
    }
    children.forEachIndexed { i, it ->
        val foundNode = it?.findTextEditText(i, dep + 1)
        if (foundNode != null) {
            return foundNode // 如果在子节点中找到了目标，立即返回该节点
        }
    }
    return null // 如果当前节点及其子节点都不包含目标，则返回 null
}
