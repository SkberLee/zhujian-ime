//! Key events from the IME shell → librime process_key.

#[derive(Debug, Clone, Copy)]
pub struct KeyEvent {
    pub keycode: i32,
    pub mask: i32,
}

#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum KeyResult {
    /// Engine consumed the key; refresh candidates / preedit.
    Handled,
    /// Pass through to the client (e.g. raw ASCII when not composing).
    NotHandled,
}

pub fn process_key(_session_id: u64, _ev: KeyEvent) -> KeyResult {
    // TODO: RimeApi::process_key
    KeyResult::NotHandled
}
