import axios from 'axios';

export function errorMessage(error: unknown): string {
    if (axios.isAxiosError(error)) {
        if (!error.response) return 'Нет соединения с сервером';
        const data = error.response.data as { error?: string } | undefined;
        return data?.error ?? `Ошибка ${error.response.status}`;
    }
    if (error instanceof Error) return error.message;
    return 'Неизвестная ошибка';
}
