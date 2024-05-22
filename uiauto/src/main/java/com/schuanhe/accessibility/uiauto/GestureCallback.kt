package com.schuanhe.accessibility.uiauto

/**
 * # GestureCallback
 *
 * @author schuanhe
 * @date 2023/4/26
 */
fun interface GestureCallback {

    fun onPerformGestureResult(sequence: Int, completedSuccessfully: Boolean)
}