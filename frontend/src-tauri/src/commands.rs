use keyring::Entry;

const SERVICE: &str = "com.example.salary";
const ACCESS_USER: &str = "access_token";
const REFRESH_USER: &str = "refresh_token";

fn entry(username: &str) -> Result<Entry, String> {
    Entry::new(SERVICE, username).map_err(|e| e.to_string())
}

#[tauri::command]
pub fn save_access_token(token: String) -> Result<(), String> {
    entry(ACCESS_USER)?
        .set_password(&token)
        .map_err(|e| e.to_string())
}

#[tauri::command]
pub fn get_access_token() -> Result<Option<String>, String> {
    match entry(ACCESS_USER)?.get_password() {
        Ok(t) => Ok(Some(t)),
        Err(keyring::Error::NoEntry) => Ok(None),
        Err(e) => Err(e.to_string()),
    }
}

#[tauri::command]
pub fn save_refresh_token(token: String) -> Result<(), String> {
    entry(REFRESH_USER)?
        .set_password(&token)
        .map_err(|e| e.to_string())
}

#[tauri::command]
pub fn get_refresh_token() -> Result<Option<String>, String> {
    match entry(REFRESH_USER)?.get_password() {
        Ok(t) => Ok(Some(t)),
        Err(keyring::Error::NoEntry) => Ok(None),
        Err(e) => Err(e.to_string()),
    }
}

#[tauri::command]
pub fn delete_tokens() -> Result<(), String> {
    for user in [ACCESS_USER, REFRESH_USER] {
        match entry(user)?.delete_credential() {
            Ok(_) => {}
            Err(keyring::Error::NoEntry) => {}
            Err(e) => return Err(e.to_string()),
        }
    }
    Ok(())
}
