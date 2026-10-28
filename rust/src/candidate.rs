//! Candidate list and commit text from Rime context.

#[derive(Debug, Clone)]
pub struct Candidate {
    pub text: String,
    pub comment: String,
}

#[derive(Debug, Clone, Default)]
pub struct CompositionView {
    pub preedit: String,
    pub candidates: Vec<Candidate>,
    pub page_size: usize,
    pub selected_index: usize,
}

pub fn get_composition(_session_id: u64) -> CompositionView {
    // TODO: get_context / get_commit
    CompositionView::default()
}

pub fn select_candidate(_session_id: u64, _index: usize) -> Option<String> {
    None
}
