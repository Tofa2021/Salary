import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useNavigate, useLocation } from 'react-router-dom';
import { useMutation } from '@tanstack/react-query';
import { login } from '../api/auth';
import { useAuthStore } from '../store/auth';
import { errorMessage } from '../lib/errorMessage';

const schema = z.object({
    username: z.string().min(1, 'Введите логин').max(50, 'Слишком длинный логин'),
    password: z.string().min(1, 'Введите пароль').max(100, 'Слишком длинный пароль'),
});

type FormData = z.infer<typeof schema>;

export default function LoginPage() {
    const navigate = useNavigate();
    const location = useLocation();
    const setAuth = useAuthStore((s) => s.setAuth);
    const [serverError, setServerError] = useState<string | null>(null);

    const from = (location.state as { from?: string })?.from ?? '/employees';

    const {
        register,
        handleSubmit,
        formState: { errors, isSubmitting },
    } = useForm<FormData>({
        resolver: zodResolver(schema),
        defaultValues: { username: '', password: '' },
    });

    const mutation = useMutation({
        mutationFn: login,
        onSuccess: (data) => {
            setAuth(data.username, data.role);
            navigate(from, { replace: true });
        },
        onError: (error) => {
            const status = (error as any)?.response?.status;
            if (status === 401) {
                setServerError('Неверный логин или пароль');
            } else if (status === 400) {
                setServerError('Проверьте введённые данные');
            } else {
                setServerError(errorMessage(error));
            }
        },
    });

    const onSubmit = (data: FormData) => {
        setServerError(null);
        mutation.mutate(data);
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100 px-4">
            <div className="w-full max-w-md">
                <div className="bg-white rounded-lg shadow-md p-8">
                    <div className="text-center mb-6">
                        <h1 className="text-2xl font-semibold text-gray-900">Вход в систему</h1>
                        <p className="text-sm text-gray-500 mt-1">Расчёт заработной платы</p>
                    </div>

                    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4" noValidate>
                        <div>
                            <label
                                htmlFor="username"
                                className="block text-sm font-medium text-gray-700 mb-1"
                            >
                                Логин
                            </label>
                            <input
                                id="username"
                                type="text"
                                autoComplete="username"
                                autoFocus
                                {...register('username')}
                                className={`w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                                    errors.username ? 'border-red-500' : 'border-gray-300'
                                }`}
                                placeholder="admin"
                                disabled={mutation.isPending}
                            />
                            {errors.username && (
                                <p className="mt-1 text-sm text-red-600">
                                    {errors.username.message}
                                </p>
                            )}
                        </div>

                        <div>
                            <label
                                htmlFor="password"
                                className="block text-sm font-medium text-gray-700 mb-1"
                            >
                                Пароль
                            </label>
                            <input
                                id="password"
                                type="password"
                                autoComplete="current-password"
                                {...register('password')}
                                className={`w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                                    errors.password ? 'border-red-500' : 'border-gray-300'
                                }`}
                                placeholder="••••••••"
                                disabled={mutation.isPending}
                            />
                            {errors.password && (
                                <p className="mt-1 text-sm text-red-600">
                                    {errors.password.message}
                                </p>
                            )}
                        </div>

                        {serverError && (
                            <div className="rounded-md bg-red-50 border border-red-200 px-3 py-2">
                                <p className="text-sm text-red-700">{serverError}</p>
                            </div>
                        )}

                        <button
                            type="submit"
                            disabled={mutation.isPending || isSubmitting}
                            className="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition"
                        >
                            {mutation.isPending ? 'Вход...' : 'Войти'}
                        </button>
                    </form>
                </div>

                <p className="text-center text-xs text-gray-400 mt-4">
                    Salary — аналитика заработной платы
                </p>
            </div>
        </div>
    );
}
