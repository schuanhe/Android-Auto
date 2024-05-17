package com.schuanhe.auto_redbook

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.schuanhe.auto_redbook", appContext.packageName)
    }


    private fun convertLink(link: String): String? {
        val pattern = "https?://[a-zA-Z./0-9]+".toRegex()
        val matcher = pattern.find(link)
        return matcher?.value
    }

    @Test
    fun testConvertLink() {
        val link = "28 老张聊职场发布了一篇小红书笔记，快来看吧！ 😆 BVosio711OJI6xT 😆 http://xhslink.com/ZxseEI，复制本条信息，打开【小红书】App查看精彩内容！"
        val convertedLink = convertLink(link)
        println(convertedLink)
        log("$convertedLink")
        assertEquals("http://xhslink.com/ZxseEI", convertedLink)
    }
}