import './App.css';
import { test } from './api/test';
import { useQuery } from '@tanstack/react-query';

export default function App() {
    const { data } = useQuery({
        queryKey: ['test'],
        queryFn: test,
        staleTime: 30_000,
        retry: 1,
    });

    return (
        <div style={{ padding: 24, fontFamily: 'system-ui' }}>
            <h1>{data?.text}</h1>
        </div>
    );
}
