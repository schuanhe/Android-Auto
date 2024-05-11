package com.schuanhe.auto_redbook

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.schuanhe.andro_auto_api.AccessibilityApi
import com.schuanhe.auto_redbook.actions.*
import com.schuanhe.auto_redbook.databinding.ActivityMainBinding
import com.schuanhe.auto.core.AutoApi
import com.schuanhe.auto.core.utils.jumpAccessibilityServiceSettings
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val actions = mutableListOf(
            BaseNavigatorAction(),
            RedBookGo(),
            AutoRedBook(),
            OKHttp(),
            object : Action() {
                override val name = "Stop"
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
