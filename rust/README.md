# rime_session（Rust 会话层）

当前为 **API stub**，尚未链接 librime。Android 壳已可明文上屏；引擎接入在本 crate 完成。

## 建议 JNI / C ABI 清单

Kotlin `external` 与 `#[no_mangle] pub extern "C"` 对齐（名称可再定）：

| 方法 | 职责 |
|------|------|
| `nativeAbiVersion` / `rime_session_abi_version` | 握手 |
| `nativeInit(sharedDir, userDir)` / `rime_session_init` | 设置目录、`RimeSetup`/`Initialize`、按需 `StartMaintenance` |
| `nativeDestroy` | 释放 session / finalize |
| `nativeProcessKey(keycode, mask)` | → `process_key`；返回是否消费 |
| `nativeGetPreedit` / `nativeGetCandidates(limit)` | 读 context，刷新候选栏 |
| `nativeSelectCandidate(index)` | 选词 |
| `nativeGetCommit` / `nativeClearComposition` | 取上屏串、清空编码 |
| `nativeSelectSchema(schemaId)` | 如 `"luna_pinyin"` |

按键映射：Android `KeyEvent` / 自定义键码 → Rime keycode（可对照同文 `Rime.kt` 的 external 列表**设计形状**，自行实现，勿抄 GPL 代码）。

## cargo-ndk 链 librime（下一步）

1. 安装 NDK，设置 `ANDROID_NDK_HOME`
2. `rustup target add aarch64-linux-android`（及需要的 abi）
3. `cargo install cargo-ndk`
4. 先为 Android 交叉编译 **librime**（及 boost/marisa/opencc 等依赖），得到各 ABI 的 `.a`/`.so`
5. 在本 crate 用 `cc` / `cmake` / `bindgen`（或 `cxx`）链上 `rime_api.h`
6. 产出 cdylib：

```bash
cd rust
cargo ndk -t arm64-v8a -o ../android/app/src/main/jniLibs \
  build --release
```

7. Kotlin：`System.loadLibrary("rime_session")`
8. 首次运行将 `assets/rime/` 拷到 app 私有目录再 `nativeInit`

模块规划见 `src/{session,key,candidate}.rs`。
