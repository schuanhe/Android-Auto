package com.schuanhe.auto_redbook.api


import com.schuanhe.auto.core.api.setScreenSize
import com.schuanhe.auto.core.api.swipe
import com.schuanhe.auto.core.viewnode.ViewNode


//@RequiresApi(Build.VERSION_CODES.N)
suspend fun redBookGo(includeInvisible: Boolean = true) {

    setScreenSize(100, 100)
    swipe(50, 80, 50, 10, 300)


//    requireBaseAccessibility()
//
//    val matchAll = listOf("^\\d{4}-\\d{2}-\\d{2}$",
//        "^\\d{2}-\\d{2}$",
//        "^\\d+(天|小时)前$",
//        "^(昨|今)天 \\d{2}:\\d{2}$")
//    var listSG: ConditionGroup = SG()
//
//    matchAll.forEachIndexed { index, regex ->
//        listSG = if (index == 0) {
//            listSG.matchText(regex)
//        } else {
//            listSG.or().matchText(regex)
//        }
//    }
//
//    val list = listSG.findAll()
//    log("搜索结果：${list.size}")
//
//
//    // 设置相对
//    setScreenSize(100, 100)
//    list.last().swipeOffset(0,-50,300)


    // 点击搜索
//    delay(1000)
//    click(1100,120);
//    delay(1000)
//    input("测试关键词")
//    click(1150,150)
//    delay(3000)
//    // 点击帖子
//    click(300,800)
//    delay(3000)
//    // 点击分享
//    click(1150,150)
//    // 点击复制链接
//    delay(1000)
//    click(600,2400)
//    // 返回
//    back()
//    delay(1000)
//    scrollUp()

//
//
//    search("搜索")
//    delay(1000)
//    input("测试关键词")


//    setScreenSize(500, 500)
//     尝试点击
//    delay(1000)
//    click(763,316);
//    print("开始点击搜索")

//
//    search2()
//    delay(3000)
//
//
//    print("开始点击帖子")
//    // 模拟点击
//    val ts0 = ViewNode.getRoot().findText(0, 0, "", "android.widget.ImageView")
//    if (ts0 != null) {
//        ts0.click()
//    }else{
//        println("没有找到")
//    }
//
//
//    delay(1000)
//    // 点击分享
//    print("开始点击分享")
//
//    val ts = ViewNode.getRoot().findDesc(0, 0, "分享")
//    ts?.click()
//    delay(1000)
//    //
//    print("开始点击复制链接")
//    val ts2 = ViewNode.getRoot().findDesc(0, 0, "复制链接")
//    ts2?.click()
//    delay(1000)
//

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
