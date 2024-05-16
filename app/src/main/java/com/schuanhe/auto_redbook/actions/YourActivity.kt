package com.schuanhe.auto_redbook.actions

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.schuanhe.auto_redbook.R

class YourActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your)

        // 显示消息
        val textView = findViewById<TextView>(R.id.message_text_view)
        textView.text = "你点击了通知！"
    }
}
