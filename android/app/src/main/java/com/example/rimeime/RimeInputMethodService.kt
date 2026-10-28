package com.example.rimeime

import android.inputmethodservice.InputMethodService
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Android IME 壳：极简 QWERTY，按键直接 [InputConnection.commitText]。
 * 后续改为：触控 → JNI → Rust rime_session → librime。
 *
 * 同文仅作阅读参考，勿复制其源码。
 */
class RimeInputMethodService : InputMethodService() {

    private var statusBar: TextView? = null

    override fun onCreateInputView(): View {
        val root = layoutInflater.inflate(R.layout.input_view, null)
        statusBar = root.findViewById(R.id.status_bar)

        val row1 = root.findViewById<LinearLayout>(R.id.row1)
        val row2 = root.findViewById<LinearLayout>(R.id.row2)
        val row3 = root.findViewById<LinearLayout>(R.id.row3)
        val row4 = root.findViewById<LinearLayout>(R.id.row4)

        fillRow(row1, listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"))
        fillRow(row2, listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"))
        fillRow(
            row3,
            listOf("z", "x", "c", "v", "b", "n", "m"),
            leadingSpecial = "⌫" to { deleteBackward() },
            trailingSpecial = "⏎" to { performEditorAction() },
        )
        fillRow(
            row4,
            emptyList(),
            centerSpace = true,
        )
        return root
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        statusBar?.text = getString(R.string.ime_subtitle)
    }

    private fun fillRow(
        row: LinearLayout,
        letters: List<String>,
        leadingSpecial: Pair<String, () -> Unit>? = null,
        trailingSpecial: Pair<String, () -> Unit>? = null,
        centerSpace: Boolean = false,
    ) {
        row.removeAllViews()
        val lp = { weight: Float ->
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, weight).apply {
                marginStart = 3
                marginEnd = 3
            }
        }

        if (centerSpace) {
            row.addView(makeKey("空格", special = true) { commit(" ") }, lp(1f))
            return
        }

        leadingSpecial?.let { (label, action) ->
            row.addView(makeKey(label, special = true, action = action), lp(1.4f))
        }
        letters.forEach { ch ->
            row.addView(makeKey(ch) { commit(ch) }, lp(1f))
        }
        trailingSpecial?.let { (label, action) ->
            row.addView(makeKey(label, special = true, action = action), lp(1.4f))
        }
    }

    private fun makeKey(
        label: String,
        special: Boolean = false,
        action: () -> Unit,
    ): Button {
        return Button(this).apply {
            text = label
            textSize = if (label.length > 1) 14f else 16f
            isAllCaps = false
            setTextColor(getColor(R.color.key_fg))
            setBackgroundResource(
                if (special) R.drawable.key_special_bg else R.drawable.key_bg,
            )
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 0)
            setOnClickListener { action() }
        }
    }

    private fun commit(text: String) {
        currentInputConnection?.commitText(text, 1)
    }

    private fun deleteBackward() {
        val ic = currentInputConnection ?: return
        // Prefer deleting selection / composing; fall back to one code unit
        val selected = ic.getSelectedText(0)
        if (!selected.isNullOrEmpty()) {
            ic.commitText("", 1)
        } else {
            ic.deleteSurroundingText(1, 0)
        }
    }

    private fun performEditorAction() {
        val ic = currentInputConnection ?: return
        val ei = currentInputEditorInfo
        val action = ei?.imeOptions?.and(EditorInfo.IME_MASK_ACTION) ?: EditorInfo.IME_ACTION_NONE
        if (action != EditorInfo.IME_ACTION_NONE && action != EditorInfo.IME_ACTION_UNSPECIFIED) {
            ic.performEditorAction(action)
        } else {
            commit("\n")
        }
    }
}
