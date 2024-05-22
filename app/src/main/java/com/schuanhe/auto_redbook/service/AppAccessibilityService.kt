package com.schuanhe.auto_redbook.service

import com.schuanhe.andro_auto_api.AccessibilityApi
import com.schuanhe.auto.core.AppScope
import com.schuanhe.auto_redbook.DemoApp.Companion.PageUpdate
import timber.log.Timber

/**
 * # MyAccessibilityService
 *
 * Created on 2024/5/22
 * @author schuanhe
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
        PageUpdate = currentScope
        Timber.tag(TAG).d("onPageUpdate: %s", currentScope)
    }

    companion object {
        private const val TAG = "MyAccessibilityService"
    }

}