/**
 * Vista per le informazioni dell'utente
 */

import { useState, useEffect } from "react";
import { Link } from "react-router-dom";

export interface InfoUtente {
    nomeUtente: string;
    email: string;
    nomeTeam: string;
}

export default function InfoUtenteView() {
    const [infoUtente, setInfoUtente] = useState<InfoUtente | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        let isMounted = true;
        const fetchData = async () => {
            const token = localStorage.getItem("token");
            if (!token) {
                if (isMounted) {
                    setError("Sessione scaduta. Effettuare di nuovo il login.");
                    setLoading(false);
                }
                return;
            }

            try {
                const response = await fetch("/api/utente/me", {
                    method: "GET",
                    headers: {
                        "Accept": "application/json",
                        "Authorization": `Bearer ${token}`,
                    }
                });

                if (!response.ok) {
                    if (response.status === 401 || response.status === 403) throw new Error("Sessione scaduta o non valida. Rieffettua il login.");
                    throw new Error(`Errore del server (Codice ${response.status})`);
                }
                if (isMounted) setInfoUtente((await response.json()));
            } catch (err: unknown) {
                if (isMounted)
                    if (err instanceof Error) setError(err.message);
                    else setError("Si è verificato un errore durante il caricamento dei dati.");
            } finally { setLoading(false); }
        };

        void fetchData();
        return () => { isMounted = false; };
    }, []);

    return (
        <div className="container profile-container">
            <header className="page-header">
                <Link to="/dashboard" className="btn-back">← Torna alla Dashboard</Link>
                <h1>Profilo Utente</h1>
            </header>

            {loading && <div className="loading-message">Caricamento informazioni in corso...</div>}

            {error && <div className="error-message">{error}</div>}

            {!loading && !error && infoUtente && (
                <div className="profile-card">
                    <div className="profile-field">
                        <strong>Nome Utente: </strong>
                        <span>{infoUtente.nomeUtente}</span>
                    </div>

                    <div className="profile-field">
                        <strong>Email: </strong>
                        <span>{infoUtente.email}</span>
                    </div>

                    <div className="profile-field">
                        <strong>Team di Appartenenza: </strong>
                        <span className={`team-badge ${infoUtente.nomeTeam === "Nessuno" ? "no-team" : "has-team"}`}>
                            {infoUtente.nomeTeam}
                        </span>
                    </div>
                </div>
            )}
        </div>
    );
}