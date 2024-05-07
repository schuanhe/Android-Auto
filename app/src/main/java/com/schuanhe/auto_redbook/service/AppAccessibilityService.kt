package com.schuanhe.auto_redbook.service

import android.util.Log
import com.schuanhe.andro_auto_api.AccessibilityApi
import com.schuanhe.auto.core.AppScope

/**
 * # MyAccessibilityService
 *
 * Created on 2020/6/10
 * @author Vove
 */
class AppAccessibilityService : AccessibilityApi() {

    //启用 页面更新 回调
    override val enableListenPageUpdate: Boolean = true

    override fun onCreate() {
        //must set
        baseService = this
        super.onCreate()
    }

    override fun onDestroy() {
        //must set
        baseService = null
        super.onDestroy()
    }

    //页面更新回调
    override fun onPageUpdate(currentScope: AppScope) {
        Log.d(TAG, "onPageUpdate: $currentScope")
    }

    companion object {
        private const val TAG = "MyAccessibilityService"
    }

}