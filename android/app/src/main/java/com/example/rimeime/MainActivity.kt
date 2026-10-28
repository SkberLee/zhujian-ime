package com.example.rimeime

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

/**
 * 安装后引导：打开系统「启用输入法」与「切换输入法」。
 */
class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }
        root.addView(
            TextView(this).apply {
                text = "竹简\n\n1. 启用本输入法\n2. 切换到本输入法\n3. 任意文本框点按字母键应直接上屏（尚未接 Rime）"
                textSize = 16f
            },
        )
        root.addView(
            Button(this).apply {
                text = "打开输入法设置"
                setOnClickListener {
                    startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
                }
            },
        )
        root.addView(
            Button(this).apply {
                text = "选择输入法"
                setOnClickListener {
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showInputMethodPicker()
                }
            },
        )
        setContentView(root)
    }
}
