import { invoke } from '@tauri-apps/api/core';

export const tokenStorage = {
    async getAccess(): Promise<string | null> {
        return invoke<string | null>('get_access_token');
    },
    async setAccess(token: string): Promise<void> {
        await invoke('save_access_token', { token });
    },

    async getRefresh(): Promise<string | null> {
        return invoke<string | null>('get_refresh_token');
    },
    async setRefresh(token: string): Promise<void> {
        await invoke('save_refresh_token', { token });
    },

    async clear(): Promise<void> {
        await invoke('delete_tokens');
    },
};
