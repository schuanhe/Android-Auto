/*
 * Copyright (c) 2024.
 */

package com.schuanhe.auto_redbook.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.schuanhe.auto_redbook.api.actAutoRedBookNoAndroid24
import com.schuanhe.auto_redbook.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * 定时任务
 */
class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // 在这里执行定时任务
        log("定时任务执行9999999999")
        // 添加你的业务逻辑
        CoroutineScope(Dispatchers.IO).launch {
            executeSuspendTask()
        }
    }

    private suspend fun executeSuspendTask() {
        actAutoRedBookNoAndroid24()
    }
}
