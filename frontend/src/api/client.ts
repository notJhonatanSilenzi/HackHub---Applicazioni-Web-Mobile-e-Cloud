/**
 * Qui avvengono tutti i controlli di reperimento, controllo e validazione del token JWT
 * per verificare l'identità dell'utente che richiede una certa transazione al nostro
 * applicativo
 */

const BASE_URL = '/api';

/**
 * Funzione centralizzata per l'esecuzione di chiamate HTTP. Aggiunge automaticamente
 * l'header Authorization se è presente un JWT in LocalStorage
 * @param endpoint
 * @param options
 */
export async function fetchApi<T = any>(endpoint: string, options: RequestInit = {}): Promise<T> {
    // 1. Recupera il JWT nel browser
    const token = localStorage.getItem('token');

    // 2. Prepara le intestazioni HTTP (Headers)
    const headers: Record<string, string> = {
        'Content-Type': 'application/json',
        ...(options.headers as Record<string, string>),
    };

    // 3. Se il token esiste, aggiunge l'header Bearer JWT
    if (token) { headers['Authorization'] = `Bearer ${token}`; }

    // 4. Esegui la richiesta HTTP usando il proxy Nginx/Vite per la rotta /api
    const response = await fetch(`${BASE_URL}${endpoint}`, {
        ...options,
        headers,
    });

    // 5. Gestione dell'errore 401 - Unauthorized
    if (response.status == 401) {
        localStorage.removeItem('token'); // Rimuovi il token
        window.location.href = '/login';
        throw new Error('Sessione scaduta oppure non autorizzata!');
    }

    // 6. Gestione di altri casi di errore
    if (!response.ok) {
        const errorData = await response.text();
        throw new Error(errorData || 'Errore nella richiesta al server');
    }

    // 7. Altrimenti la richiesta è andata a buon fine. Ritorna json oppure text in modo intelligente
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
        return await response.json();
    }
    return (await response.text()) as unknown as T;
}