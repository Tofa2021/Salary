import { useNavigate } from 'react-router-dom';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { logout } from '../api/auth';
import { useAuthStore } from '../store/auth';

export default function EmployeesPage() {
    const navigate = useNavigate();
    const queryClient = useQueryClient();
    const username = useAuthStore((s) => s.username);
    const role = useAuthStore((s) => s.role);
    const clear = useAuthStore((s) => s.clear);

    const logoutMutation = useMutation({
        mutationFn: logout,
        onSuccess: () => {
            clear();
            queryClient.clear();
            navigate('/login', { replace: true });
        },
    });

    return (
        <div className="min-h-screen bg-gray-50">
            <header className="bg-white shadow-sm">
                <div className="max-w-7xl mx-auto px-4 py-3 flex justify-between items-center">
                    <h1 className="text-lg font-semibold">Сотрудники</h1>
                    <div className="flex items-center gap-4">
                        <span className="text-sm text-gray-600">
                            {username ?? '—'} <span className="text-gray-400">({role ?? '—'})</span>
                        </span>
                        <button
                            onClick={() => logoutMutation.mutate()}
                            disabled={logoutMutation.isPending}
                            className="text-sm text-red-600 hover:underline disabled:opacity-50"
                        >
                            Выйти
                        </button>
                    </div>
                </div>
            </header>
            <main className="max-w-7xl mx-auto px-4 py-6">
                <p className="text-gray-500">Таблица сотрудников — следующий шаг.</p>
            </main>
        </div>
    );
}
