import api from './client';

export interface TestDto {
    text: string;
}

export async function test(): Promise<TestDto> {
    const { data } = await api.get<TestDto>('/test');
    console.log('[test.ts] axios data:', data);
    console.log('[test.ts] typeof data:', typeof data);
    return data;
}
