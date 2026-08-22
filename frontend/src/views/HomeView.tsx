/**
 * Vista della Home principale
 */

import { useState, useEffect } from "react";
import { Link } from "react-router-dom";

// Interfaccia per esporre le info pubbliche degli hackathon nella Home
export interface Hackathon {
    nome: string;
    dataInizio: string; // ISO String del backend
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

export default function HomeView() {
    const [hackathons, setHackathons] = useState<Hackathon[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        let isMounted = true;
        const fetchHackathons = async () => {
            try {
                const response = await fetch('/api/hackathon');
                if (!response.ok) {
                    // Stampa il codice esatto (es. 404, 500, 403) nella console F12
                    console.error(`HTTP error! Status: ${response.status}`);
                    throw new Error(`Errore dal server (Codice ${response.status})`);
                }
                const data: Hackathon[] = await response.json();
                if (isMounted) setHackathons(data);
            } catch (err: unknown) {
                if (isMounted)
                    if (err instanceof Error) setError(err.message);
                    else setError('Si è verificato un errore anomalo');
            } finally {
                if (isMounted) setLoading(false);
            }
        };

        // Usiamo 'void' per indicare esplicitamente a TypeScript/IntelliJ che l'esecuzione async è intenzionale
        void fetchHackathons();

        return () => { isMounted = false; };
    }, []);

    if (loading) return <div className="container">Caricamento in corso...</div>;
    if (error) return <div className="container">{error}</div>;

    return (
        <div className="container">
            <h1>Info Pubbliche Hackathon</h1>
            {hackathons.length === 0 ? (
                <div>
                    <p>Nessun hackathon presente al momento</p>
                </div>)
            : (
                    <div className={"hackathon-grid"}>
                        {hackathons.map((h, index) => (
                            <div key={index} className="hackathon-card">
                                <div className={"hackathon-header"}>
                                    <h3>{h.nome}</h3>
                                    <span className={`badge ${h.stato.toLowerCase()}`}>{h.stato}</span>
                                </div>
                                <p>{h.luogo}</p>
                                <div>Premio: {h.premio}€</div>
                                <div>
                                    <p><strong>Date:</strong> {new Date(h.dataInizio).toLocaleDateString()} - {new Date(h.dataFine).toLocaleDateString()}</p>
                                    <p><strong>Scadenza Iscrizioni:</strong> {new Date(h.scadenzaIscrizioni).toLocaleDateString()}</p>
                                </div>
                                <div>
                                    <p><strong>Dimensioni Team:</strong> {h.teamMin} - {h.teamMax}</p>
                                    <p><strong>Posti Disponibili:</strong> {h.postiRimanenti}</p>
                                </div>
                                {h.regolamento && (
                                    <div>
                                        <p>{h.regolamento}</p>
                                    </div>
                                )}
                                <div className={"hackathon-footer"}>
                                    <Link to="/login" className={"btn-primary"}>Iscrivi il tuo team</Link>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
        </div>
    );
}