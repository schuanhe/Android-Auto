/*
 * Copyright (c) 2024.
 */

package com.schuanhe.auto_redbook.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import java.util.Calendar



/**
 * 设置定时任务
 * @param hour 小时
 * @param minute 分钟
 * @param alarmManager AlarmManager 对象
 * @param pendingIntent PendingIntent 对象
 */
public fun setDailyAlarm(hour: Int, minute: Int, alarmManager: AlarmManager, pendingIntent: PendingIntent) {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
    }

    // 如果设置的时间已经过去，设定为第二天
    if (calendar.timeInMillis < System.currentTimeMillis()) {
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }

    alarmManager.setInexactRepeating(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        AlarmManager.INTERVAL_DAY,
        pendingIntent
    )
}

/**
 * 取消定时任务
 */
public fun cancelAlarm(alarmManager: AlarmManager, pendingIntent: PendingIntent) {
    alarmManager.cancel(pendingIntent)
}