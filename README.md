# 竹简（zhujian-ime）

用 Rust + librime 从零开发的 **Android 输入法**「竹简」（当前范围：仅 Android MVP）。

> 远期或可把 Rust 会话层复用到其它端；**现阶段不做** Windows / iOS / Linux。

## 当前目标（Android-only MVP）

1. **Kotlin 壳**：`InputMethodService` 弹出极简键盘，按键能 `commitText` 上屏（已实现明文路径，尚未接引擎）
2. **Rust 会话层**：`rime_session` stub → 日后 JNI + librime
3. **方案数据**：assets 内已放入 **luna 全拼** 相关 YAML/词库（仅 Rime 数据，非同文代码），供接入引擎后 deploy
4. **参考**：同文 Trime 只读架构/JNI 形状，**不 fork、不复制其源码**（GPL）

## 本机构建与试用

### 环境

- JDK **17**
- Android Studio（Ladybug / 近年版本即可）+ Android SDK（compileSdk 35）
- 模拟器或真机（API 24+）

### 打开工程

1. Android Studio → **Open** → 选择本仓库的 **`android/`** 目录（不是仓库根，除非你自行改成多模块根）
2. 等待 Gradle Sync（首次会下载 Gradle 8.11.1 与依赖）
3. Run `app` 安装到设备

### 启用输入法

1. 打开已安装的 **竹简** 应用 → 「打开输入法设置」→ 启用 **竹简**
2. 「选择输入法」切到本 IME  
   或：系统设置 → 系统 → 语言和输入法 → 屏幕键盘
3. 任意文本框调出键盘：点字母应 **直接上屏明文**（证明壳可用）
4. 退格 / 空格 / 回车可用

### 命令行（可选）

```bash
cd android
./gradlew :app:assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

本 CI/沙箱未必有 JDK/SDK；以本机 Android Studio 为准。

## 仓库结构

```
zhujian-ime/
├── README.md
├── REPORT.md                 # 同文路径调研（只读参考）
├── docs/ARCHITECTURE.md
├── rust/                     # rime_session stub + README（cargo-ndk / JNI 清单）
└── android/                  # 可打开的 Gradle 工程
    └── app/src/main/
        ├── java/.../RimeInputMethodService.kt
        ├── assets/rime/      # luna 全拼等方案数据 + NOTICE
        └── res/
```

## 方案数据与许可证

路径：`android/app/src/main/assets/rime/`  

见其中 **`NOTICE.txt`**。来源为 rime-prelude / rime-luna-pinyin / rime-essay（经 Trime 子模块拷贝的**数据文件**），多为 **LGPL-3.0**（以各 `LICENSE.*` 为准）。**未**复制 `trime.yaml` 或同文 Kotlin/JNI。

## 下一步开发

1. 本机跑通明文键盘（本 README 上文）
2. 按 [rust/README.md](rust/README.md) 用 cargo-ndk 链 librime，暴露 init / process_key / get_candidates / commit
3. Kotlin 按键改为走 JNI；preedit + 候选栏替换「直接 commit 字母」
4. 首次启动把 `assets/rime` deploy 到 app 私有目录后选 `luna_pinyin`

## 开发约定

- 引擎能力交给 librime；壳只做 UI、按键映射、用户目录
- 全拼 / 双拼 / 五笔 = 换 schema，不换引擎
- 禁止把同文源码粘进本仓库
