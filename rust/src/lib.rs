//! rime_session — thin session layer above librime.
//!
//! Module plan (stubs only; no librime FFI yet):
//! - [`session`]: create/destroy Rime session, deploy check
//! - [`key`]: map shell key events → Rime process_key
//! - [`candidate`]: context, page, select, commit text
//!
//! Android loads this as `System.loadLibrary("rime_session")` after JNI glue exists.

#![allow(dead_code)]

pub mod session;
pub mod key;
pub mod candidate;

/// Placeholder ABI version for future JNI handshake.
pub const ABI_VERSION: u32 = 1;

#[no_mangle]
pub extern "C" fn rime_session_abi_version() -> u32 {
    ABI_VERSION
}
