export type UserRole = 'ADMIN' | 'ACCOUNTANT' | 'HR' | 'ANALYST' | 'VIEWER';

export interface LoginRequest {
    username: string;
    password: string;
}

export interface AuthResponse {
    accessToken: string;
    refreshToken: string;
    username: string;
    role: UserRole;
    expiresIn: number;
}
