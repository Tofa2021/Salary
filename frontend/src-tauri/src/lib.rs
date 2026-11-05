mod commands;

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    tauri::Builder::default()
        .plugin(tauri_plugin_dialog::init())
        .invoke_handler(tauri::generate_handler![
            commands::save_access_token,
            commands::get_access_token,
            commands::save_refresh_token,
            commands::get_refresh_token,
            commands::delete_tokens,
        ])
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
