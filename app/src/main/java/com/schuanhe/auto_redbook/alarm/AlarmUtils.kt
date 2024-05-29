/*
 * Copyright (c) 2024.
 */

package com.schuanhe.auto_redbook.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import com.schuanhe.auto_redbook.MainActivity.Companion.alarmManager
import com.schuanhe.auto_redbook.MainActivity.Companion.pendingIntent
import com.schuanhe.auto_redbook.log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


/**
 * 全局变量
 */

// 下次 定时任务时间
public var nextAlarmTime: Long = 0


/**
 * 设置定时任务
 * @param hour 小时
 * @param minute 分钟
 * @param alarmManager AlarmManager 对象
 * @param pendingIntent PendingIntent 对象
 */
public fun setDailyAlarm(hour: Int, minute: Int) {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
    }

    // 如果设置的时间已经过去，设定为第二天
    if (calendar.timeInMillis < System.currentTimeMillis()) {
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }

    nextAlarmTime = calendar.timeInMillis

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

/**
 * 获取定时任务时间
 */

public fun getNextAlarmTime() {
    val date = Date(nextAlarmTime)
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val formattedDate =sdf.format(date)
//    Toast.makeText(INS, "定时:$formattedDate", Toast.LENGTH_LONG).show()
    log("定时:$formattedDate[$nextAlarmTime]")

}