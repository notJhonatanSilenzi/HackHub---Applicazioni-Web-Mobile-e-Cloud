/**
 * Componente typescript isolata che restituisce la lista di hackathon tramite chiamata
 * http pubblica, senza Jwt
 */

export interface Hackathon { // Interfaccia per rappresentare un hackathon nel frontend
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

/**
 * Funzione asincrona per gestire eventuali errori nella fetch degli hackathon
 */
export async function fetchHackathons(): Promise<Hackathon[]> {
    const response = await fetch("/api/hackathon", { // Effettua la fetch
        method: "GET",
        headers: {
            "Accept": "application/json"
        }
    });

    if (!response.ok) { // Gestione del messaggio di errore
        console.error(`HTTP error! Status: ${response.status}`);
        throw new Error(`Errore dal server (Codice ${response.status})`);
    }

    return await response.json(); // Attesa della risposta
}