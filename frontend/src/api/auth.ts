/**
 * Modulo per la gestione delle chiamate HTTP di login e registrione
 */

import { fetchApi } from "./client.ts";

export interface LoginRequest { // interfaccia per richiedere un'autenticazione
    nomeUtente: string;
    password: string;
}
export interface RegisterRequest { // interfaccia per richiedere una registrazione
    nomeUtente: string;
    email: string;
    password: string;
}
export interface AuthResponse { // interfaccia per esporre un token JWT dopo un'auth
    token: string;
}

/**
 * Funzione per gestire la chiamata di login. Invia le credenziali al backend, riceve
 * il JWT e lo salva in localStorage
 */
export async function login(data: LoginRequest): Promise<AuthResponse> {
    const res = await fetchApi<AuthResponse>("/autenticazione/accesso", {
        method: "POST",
        body: JSON.stringify(data),
    });
    // Distinguo se viene tornato un JSON con un JWT oppure la stringa token
    const jwt = typeof res === "string" ? res : res.token;
    if (jwt) localStorage.setItem("token", jwt);
    return res;
}

/**
 * Funzione per aggiungere un nuovo utente al database
 */
export async function register(data: RegisterRequest): Promise<any> {
    return await fetchApi("/autenticazione/registrazione", {
        method: "POST",
        body: JSON.stringify(data),
    });
}