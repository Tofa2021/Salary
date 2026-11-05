import { create } from 'zustand';
import type { UserRole } from '../types/api';

interface AuthState {
    username: string | null;
    role: UserRole | null;
    isAuthenticated: boolean;
    initialized: boolean;
    setAuth: (username: string, role: UserRole) => void;
    setInitialized: () => void;
    clear: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
    username: null,
    role: null,
    isAuthenticated: false,
    initialized: false,
    setAuth: (username, role) => set({ username, role, isAuthenticated: true }),
    setInitialized: () => set({ initialized: true }),
    clear: () => set({ username: null, role: null, isAuthenticated: false }),
}));
