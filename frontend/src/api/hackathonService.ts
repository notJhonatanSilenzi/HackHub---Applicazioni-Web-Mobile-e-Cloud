/**
 * Componente typescript isolata che restituisce la lista di hackathon tramite chiamata
 * http pubblica, senza Jwt
 */

export interface Hackathon {
    nome: string;
    dataInizio: string;
    dataFine: string;
    luogo: string;
    premio: number;
    teamMin: number;
    teamMax: number;
    regolamento: string;
    scadenzaIscrizioni: string;
    stato: string;
    numeroTeamIscritti: number;
    maxIscrizioni: number;
    postiRimanenti: number;
    regolamentoDisponibile: string;
}

export async function fetchHackathons(): Promise<Hackathon[]> {
    const response = await fetch('/api/hackathon', {
        method: 'GET',
        headers: {
            'Accept': 'application/json'
        }
    });

    if (!response.ok) {
        console.error(`HTTP error! Status: ${response.status}`);
        throw new Error(`Errore dal server (Codice ${response.status})`);
    }

    return await response.json();
}