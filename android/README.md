# 竹简 — Android 工程

用 **Android Studio 打开本目录（`android/`）**。

## 模块

| 组件 | 说明 |
|------|------|
| `RimeInputMethodService` | IME 入口；极简键盘明文上屏（系统显示名：**竹简**） |
| `MainActivity` | 引导启用 / 切换输入法 |
| `assets/rime/` | luna 全拼等方案数据（尚未被引擎加载） |

## 构建

- JDK 17；`./gradlew :app:assembleDebug`
- 详见仓库根 [README.md](../README.md)「本机构建与试用」

## 下一步（引擎）

见 [../rust/README.md](../rust/README.md)。Kotlin 侧将增加 `external` JNI，替换直接 `commitText` 字母。
