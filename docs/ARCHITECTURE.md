# 竹简 — 架构说明（Android-only MVP）

当前范围：**只做 Android 输入法「竹简」**。跨端（Windows / iOS / Linux）仅为远期可能，不在本阶段实现。

## 分层

```
┌─────────────────────────────────────────────────────────┐
│  Kotlin 壳（android/）                                    │
│  - RimeInputMethodService：生命周期、键盘 UI、上屏         │
│  - MVP 现状：字母键 → InputConnection.commitText（明文） │
│  - 下一步：按键 → JNI → Rust → 候选 / commit / preedit   │
└──────────────────────────┬──────────────────────────────┘
                           │ JNI（待做）
┌──────────────────────────▼──────────────────────────────┐
│  Rust：rime_session（stub）                               │
│  - init / process_key / get_candidates / commit          │
└──────────────────────────┬──────────────────────────────┘
                           │ FFI（待做）
┌──────────────────────────▼──────────────────────────────┐
│  librime + assets/rime（luna 全拼等 YAML，已打包待 deploy）│
└─────────────────────────────────────────────────────────┘
```

## 空键盘 / 明文路径（已打通）

1. 系统绑定 `RimeInputMethodService`（Manifest + `res/xml/method.xml`）；系统列表显示名为 **竹简**
2. `onCreateInputView` 加载 `res/layout/input_view.xml`，代码填充 QWERTY
3. 点击 → `currentInputConnection.commitText(...)`  
   退格 → `deleteSurroundingText`；回车 → `performEditorAction` 或 `\n`

此路径**不依赖** Rust / librime，用于验证 IME 注册与上屏。

## 方案（全拼等）

数据在 `android/app/src/main/assets/rime/`（见 `NOTICE.txt`）。接入引擎后：复制/部署到 `shared_data_dir` / `user_data_dir`，维护 `luna_pinyin`。双拼、五笔他日加 schema 即可。

## 与同文

只读参考 JNI/生命周期；**不复制** GPL 源码。路径见仓库根 `REPORT.md`。
