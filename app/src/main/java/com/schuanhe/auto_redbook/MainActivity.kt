package com.schuanhe.auto_redbook

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.schuanhe.andro_auto_api.AccessibilityApi
import com.schuanhe.auto.core.AutoApi
import com.schuanhe.auto.core.utils.jumpAccessibilityServiceSettings
import com.schuanhe.auto_redbook.actions.Action
import com.schuanhe.auto_redbook.actions.AutoRedBookNoAndroid24
import com.schuanhe.auto_redbook.actions.PickScreenText
import com.schuanhe.auto_redbook.actions.SetNextAlarmTime
import com.schuanhe.auto_redbook.actions.getNextAlarmTime66
import com.schuanhe.auto_redbook.alarm.MyBroadcastReceiver
import com.schuanhe.auto_redbook.alarm.setDailyAlarm
import com.schuanhe.auto_redbook.api.createNotificationChannel
import com.schuanhe.auto_redbook.databinding.ActivityMainBinding
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var alarmManager: AlarmManager
        lateinit var pendingIntent: PendingIntent
    }

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 导入通知
        createNotificationChannel(this)
        // 显示通知showNotification
//        showNotification(this)
        // 启动定时任务

        // 确保在使用之前初始化 alarmManager
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(this, MyBroadcastReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // 默认5：00
        setDailyAlarm(5, 0, 3)

        val actions = mutableListOf(
            AutoRedBookNoAndroid24(),
            getNextAlarmTime66(),
            SetNextAlarmTime(),
            PickScreenText(),
            object : Action() {
                override val name = "暂停"
                override suspend fun run(act: ComponentActivity) {
                    actionJob?.cancel()
                }
            }
        )

        binding.listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, actions)
        binding.listView.setOnItemClickListener { _, _, position, _ ->
            onActionClick(actions[position])
        }
        binding.acsCb.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked && !AccessibilityApi.isServiceEnable) {
                buttonView.isChecked = false
                jumpAccessibilityServiceSettings(AccessibilityApi.BASE_SERVICE_CLS)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        binding.acsCb.isChecked = AccessibilityApi.isServiceEnable
        binding.acsCb.isEnabled = AutoApi.serviceType != AutoApi.SERVICE_TYPE_INSTRUMENTATION

        binding.workMode.text = "工作模式：${
            mapOf(
                AutoApi.SERVICE_TYPE_NONE to "无",
                AutoApi.SERVICE_TYPE_ACCESSIBILITY to "无障碍",
                AutoApi.SERVICE_TYPE_INSTRUMENTATION to "Instrumentation",
            )[AutoApi.serviceType]
        } "
    }

    var actionJob: Job? = null

    private fun onActionClick(action: Action) {
        if (action.name == "Stop") {
            actionJob?.cancel()
            return
        }
        if (actionJob?.isCompleted.let { it != null && !it }) {
            toast("请先停止当前任务")
            return
        }
        actionJob = launchWithExpHandler {
            action.run(this@MainActivity)
        }
        actionJob?.invokeOnCompletion {
            if (it is CancellationException) {
                Timber.tag("MainActivity").i("执行取消")
            } else if (it == null) {
                Timber.tag("MainActivity").i("执行完成")
            }
        }
    }

    override fun onDestroy() {
        actionJob?.cancel()
        super.onDestroy()
    }
}
