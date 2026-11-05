import { Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import EmployeesPage from './pages/EmployeesPage';
import ProtectedRoute from './components/ProtectedRoute';
import { tokenStorage } from './lib/token';
import { useEffect } from 'react';
import api from './api/client';
import { useAuthStore } from './store/auth';

export default function App() {
    const setInitialized = useAuthStore((s) => s.setInitialized);
    const setAuth = useAuthStore((s) => s.setAuth);
    const clear = useAuthStore((s) => s.clear);
    const initialized = useAuthStore((s) => s.initialized);

    useEffect(() => {
        (async () => {
            const token = await tokenStorage.getAccess();
            if (token) {
                try {
                    const { data } = await api.get('/auth/me');
                    setAuth(data.username, data.role);
                } catch {
                    await tokenStorage.clear();
                    clear();
                }
            }
            setInitialized();
        })();
    }, [setAuth, setInitialized, clear]);

    if (!initialized) {
        return <div className="min-h-screen flex items-center justify-center">Загрузка...</div>;
    }

    return (
        <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route element={<ProtectedRoute />}>
                <Route path="/employees" element={<EmployeesPage />} />
                <Route path="/" element={<Navigate to="/employees" replace />} />
            </Route>
            <Route path="*" element={<Navigate to="/employees" replace />} />
        </Routes>
    );
}
