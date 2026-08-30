/**
 * Componente TypeScript che effettua la fetch della chiamata pubblica per vedere gli
 * hackathon, e ne gestisce l'esito in caso di riuscita oppure di errore
 */

import { useState, useEffect } from "react";
import { fetchHackathons } from "./hackathonService.ts";
import type { Hackathon } from "./hackathonService.ts";

export function useHackathons() {
    const isAuthenticated = Boolean(localStorage.getItem("token")); // Controllo se c'è un token in localStorage
    const [hackathons, setHackathons] = useState<Hackathon[]>([]); // Lista degli hackathon ricavati dalla fetch
    const [loading, setLoading] = useState(true); // Caricamento in corso
    const [error, setError] = useState<string | null>(null); // Eventuale messaggio di errore

    useEffect(() => {
        let isMounted = true; // Boolean per prevenire race condition per chiamate http asincrone

        const loadData = async () => {
            try {
                const data = await fetchHackathons();
                if (isMounted) setHackathons(data);
            } catch (err: unknown) {
                if (isMounted) { // Gestione dell'errore
                    if (err instanceof Error) setError(err.message);
                    else setError("Si è verificato un errore anomalo");
                }
            } finally { if (isMounted) setLoading(false); }
        };

        void loadData(); // Caricamento dei dati

        return () => { isMounted = false; }; // Caricamento concluso
    }, []);

    return { hackathons, loading, error, isAuthenticated };
}