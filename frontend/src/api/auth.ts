import { tokenStorage } from '../lib/token';
import { AuthResponse, LoginRequest } from '../types/api';
import api from './client';

export async function login(request: LoginRequest): Promise<AuthResponse> {
    const { data } = await api.post<AuthResponse>('/auth/login', request);
    await tokenStorage.setAccess(data.accessToken);
    await tokenStorage.setRefresh(data.refreshToken);
    return data;
}

export async function refresh(): Promise<AuthResponse> {
    const refreshToken = await tokenStorage.getRefresh();
    const { data } = await api.post<AuthResponse>('auth/refresh', { refreshToken });
    await tokenStorage.setAccess(data.accessToken);
    await tokenStorage.setRefresh(data.refreshToken);
    return data;
}

export async function logout(): Promise<void> {
    const refreshToken = await tokenStorage.getRefresh();
    await api.post('/auth/logout', { refreshToken });
    tokenStorage.clear();
}

export async function me() {
    const { data } = await api.get('auth/me');
    return data;
}
