package com.schuanhe.auto_redbook.api

import com.schuanhe.auto_redbook.log
import java.lang.reflect.Method


suspend fun redBookGo(includeInvisible: Boolean = true) {



}

fun run() {


// 调用方法
//    val result = method.invoke(messageFormat)

    var myClass: MyClass = MyClass()
    val method: Method = MyClass::class.java.getMethod("myPublicMethod",
        String::class.java
    )

    val result = method.invoke(myClass, "aa")

}

public class MyClass {
    public fun myPublicMethod(mef:String) {
        println("This is a public method.")
        log("This is a public method.$mef")
    }

    internal fun myInternalMethod() {
        println("This is an internal method.")
    }

    private fun myPrivateMethod() {
        println("This is a private method.")
    }
}