> 产品：**竹简**（仓库 `zhujian-ime`）。范围已收窄为 **Android-only MVP**（见 README）。本文件仍为同文只读路径备忘。

# 同文（Trime）参考路径调研

调研时间：2026-10-28（Asia/Shanghai）  
参考树：`/workspace/refs/trime`（浅克隆主仓 + 部分 submodule）  
对照引擎：`/workspace/refs/librime`（独立 shallow clone，约 11M）

## 克隆状态

| 路径 | 状态 | 约体积 |
|------|------|--------|
| `/workspace/refs/trime` | 主仓 OK（`.git` @ `a047f74`）；全量 `--recursive` 过慢已中止 | ~47M |
| `app/data/rime/{essay,luna-pinyin,prelude,stroke}` | 已完整 depth-1 检出 | essay ~5.7M 等 |
| `app/src/main/jni/librime` | **工作树空**（仅 gitlink）；请用旁边独立 clone | — |
| `jni/librime-lua` / `octagram` | 目录多为空 gitlink | — |
| `jni/librime-lua-deps` / `librime-predict` | 部分检出（非构建所必需） | — |
| `jni/snappy` | 未检出 | — |
| `/workspace/refs/librime` | 独立 shallow clone，读头文件/文档用 | ~11M |

说明：JNI 插件源码与同文自研 `librime_jni` 在主仓内已完整；缺的是上游 librime 源码子模块。开发 FFI 时读 `/workspace/refs/librime` 即可，不必等 Trime 内嵌 submodule。

若需补齐 Trime 内嵌引擎（体积大、耗时长）：

```bash
cd /workspace/refs/trime
git submodule update --init --depth 1 -- app/src/main/jni/librime
# 可选插件：
# git submodule update --init --depth 1 -- app/src/main/jni/librime-lua ...
```

## 关键路径（只读参考，勿拷贝进本仓库）

### InputMethodService

| 角色 | 路径 |
|------|------|
| IME 主类 | `app/src/main/java/com/osfans/trime/ime/core/TrimeInputMethodService.kt`（`open class … : LifecycleInputMethodService()`） |
| Lifecycle 基类 | `…/ime/core/LifecycleInputMethodService.kt` |
| Manifest 注册 | `app/src/main/AndroidManifest.xml` → `android:name=".ime.core.TrimeInputMethodService"` + `BIND_INPUT_METHOD` + `meta-data android.view.im` |
| 键盘 / 候选 UI | `…/ime/keyboard/KeyboardView.kt`、`…/ime/composition/CandidatesView.kt` 等 |

### JNI ↔ librime

| 角色 | 路径 |
|------|------|
| JNI 目录 | `app/src/main/jni/librime_jni/` |
| 入口 | `librime_jni/rime_jni.cc`（`#include <rime_api.h>`，单例 `Rime`，`rime_get_api()`） |
| 其它 | `key.cc`、`frontend.cc`、`session.h`、`config.cc`、`levers.cc`、`opencc.cc`、`CMakeLists.txt` |
| Kotlin 桥 | `app/src/main/java/com/osfans/trime/core/Rime.kt`：`System.loadLibrary("rime_jni")` + 大量 `external fun`（`startupRime` / `processRimeKey` / `getRimeContext` / `selectRimeSchema` …） |
| 原生构建 | `app/src/main/jni/CMakeLists.txt`、`Makefile`（`rime_jni` target）、`build-logic/.../NativeAppConventionPlugin.kt` |

**许可证**：`librime_jni` 与 Kotlin 标注 **GPL-3.0-or-later** — 本绿场项目只对照 API 形状自写，禁止粘贴同文源码。

### Schema / 词库如何打包

| 角色 | 路径 |
|------|------|
| 打进 APK 的共享数据 | `app/src/main/assets/shared/`（已含 `default.yaml`、`luna_pinyin*.schema.yaml`、`luna_pinyin.dict.yaml`、`essay.txt`、`stroke.*`、`trime.yaml` 等） |
| Git 子模块源（构建/同步用） | `app/data/rime/essay`、`luna-pinyin`、`prelude`、`stroke`（`.gitmodules`） |
| OpenCC 资源插件 | `build-logic/convention/.../OpenCCDataPlugin.kt` → `assets/shared/opencc` |
| assets 校验和 | `DataChecksumsPlugin.kt` |

运行时典型流向：assets `shared/` → 应用私有目录（user/shared data）→ librime `deploy` / `start_maintenance`。

全拼示例：`luna_pinyin` / `luna_quanpin`；笔画：`stroke`。双拼、五笔需另加 schema（如雾凇双拼、wubi），不必改引擎。

### 其它有用入口

- 应用：`com.osfans.trime.TrimeApplication`
- Daemon：`…/daemon/RimeDaemon.kt`
- 数据 Provider：`…/provider/RimeDataProvider.kt`

## 对本绿场项目（竹简）的含义

1. **Kotlin**：自写 `RimeInputMethodService`（已有 stub），对照同文生命周期，不抄类实现。  
2. **JNI**：自写薄胶水加载 `librime_session`（Rust cdylib），API 可参考 `Rime.kt` 的 `external` 列表设计，但实现走 Rust→librime。  
3. **Schema**：MVP 先打一套全拼进 `assets`；再加双拼/五笔 YAML。  
4. **引擎源码**：用 `/workspace/refs/librime`，勿依赖未拉完的 Trime `jni/librime` submodule。

## 绿场树核对

```
zhujian-ime/
├── README.md
├── REPORT.md          ← 本文件
├── docs/ARCHITECTURE.md
├── rust/              ← crate rime_session
└── android/           ← Manifest + Service stub + method.xml
```
