package com.schuanhe.auto_redbook

import com.schuanhe.accessibility.uiauto.AutoInstrumentation

/**
 * # MyInstrumentation
 *
 * @author Vove
 * @date 2023/4/25
 */
@Suppress("unused")
class MyInstrumentation : AutoInstrumentation() {

    override fun onStart() {
        super.onStart()
        startActivitySync(context.packageManager.getLaunchIntentForPackage(context.packageName))
    }


}