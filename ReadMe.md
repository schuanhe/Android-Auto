# Android-Auto

利用无障碍权限，自动完成自动化测试

比如说小红书的自动化测试

## 文档

### 1. 开发基本步骤
1. 获取无障碍权限
2. 使用工具函数输出视图结果
3. 对获取的视图进行操作
4. 提交拿到数据的结果

### 1.1 获取无障碍权限
只用使用`waitBaseAccessibility(60000)` 方法获取无障碍权限,传参是等待时间，如果获取成功，则继续往下执行，否则会抛出异常
如果存在权限则跳过获取权限步骤


### 1.2 使用工具函数输出视图结果

使用函数`printLayoutInfo()`可输出当前页面的所有视图信息
已经封装到了`ForegroundService`的通知栏上，点击**输出布局**即可

### 1.3 对获取的视图进行操作

获取到视图后查看视图结构，使用SF搜索视图


搜索视图我们使用SF工具，下面是示例
```Kotlin
// 获取id=123，并且text="搜索"的视图
SF.id(123).and().text("搜索").find()
// 获取desc="分享"，并且点击
SF.desc("分享").click()
// 暂停1s等待视图加载
delay(1000)
// 点击坐标点
// 手势可以参考【gesture_api.kt】
// 在对屏幕坐标点操作时候，可以设置全局相对坐标点，这样就可以通过相对坐标点击控件
setScreenSize(100, 100)

// 点击坐标点(50,50),时间1ms
pressWithTime(50, 50, 100)
// 长按坐标点(50,50)
longClick(50, 50)

// 俩个坐标之间滑动
/**
 * 两点间滑动
 * @param x1 Int
 * @param y1 Int
 * @param x2 Int
 * @param y2 Int
 */
swipe(50, 50, 50, 50, 1000)

```

可以参考小红书整个流程
[AutoRedBook](https://github.com/schuanhe/Android-Auto/blob/master/app/src/main/java/com/schuanhe/auto_redbook/api/AutoRedBook.kt)


[AutoRedBookUtils](https://github.com/schuanhe/Android-Auto/blob/master/app/src/main/java/com/schuanhe/auto_redbook/api/AutoRedBookUtils.kt)

下面是常用的函数

|                              方法                              |                        说明                        |
|:------------------------------------------------------------:|:------------------------------------------------:|
|   findFirst(includeInvisible: Boolean = false): ViewNode?    | 立即搜索，返回满足条件的第一个结果<br>includeInvisible: 是否包含不可见元素 |
| findAll(includeInvisible: Boolean = false): Array\<ViewNode> |                 立即搜索，返回满足条件的所有结果                 |
|         waitFor(waitMillis: Long = 30000): ViewNode?         |          等待搜索，在指定时间内循环搜索（视图更新），超时返回null          |
|      require(waitMillis: Long = WAIT_MILLIS): ViewNode       |                     等待超时抛出异常                     |
|         findByDepths(vararg depths: Int): ViewNode?          |                     指定深度索引搜索                     |


更多 `Api` 可在下列文件查看

core/src/main/java/com/schuanhe/auto/core/api/nav_api.kt

- [view_finder_api.kt](https://github.com/schuanhe/Android-Auto/tree/master/core/src/main/java/com/schuanhe/auto/core/api/view_finder_api.kt)
- [gesture_api.kt](https://github.com/schuanhe/Android-Auto/tree/master/core/src/main/java/com/schuanhe/auto/core/api/gesture_api.kt)
- [nav_api.kt](https://github.com/schuanhe/Android-Auto/tree/master/core/src/main/java/com/schuanhe/auto/core/api/nav_api.kt)
- [ViewNode](https://github.com/schuanhe/Android-Auto/tree/master/core/src/main/java/com/schuanhe/auto/core/viewnode/ViewNode.kt)
- [SmartFinderConditions.kt](https://github.com/schuanhe/Android-Auto/tree/master/core/src/main/java/com/schuanhe/auto/core/viewfinder/SmartFinderConditions.kt)