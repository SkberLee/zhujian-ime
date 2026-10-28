//! Session lifecycle: init librime traits (shared/user data dirs), create session id.
//!
//! Next: wrap `RimeApi::{setup,initialize,start_maintenance,create_session,...}`.

#[derive(Debug, Default)]
pub struct SessionConfig {
    pub shared_data_dir: String,
    pub user_data_dir: String,
}

#[derive(Debug)]
pub struct Session {
    // Will hold Rime session_id / opaque handle once FFI is wired.
    pub id: u64,
}

impl Session {
    pub fn new(_cfg: &SessionConfig) -> Result<Self, String> {
        // TODO: call librime via cxx/bindgen
        Err("librime FFI not linked yet".into())
    }

    pub fn destroy(&mut self) {
        // TODO: destroy_session
    }
}
