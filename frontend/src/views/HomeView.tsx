/**
 * Vista della Home principale
 */

import { useState, useEffect } from "react";
import { Link } from "react-router-dom";

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

export default function HomeView() {
    const isAuthenticated = Boolean(localStorage.getItem('token'));
    const [hackathons, setHackathons] = useState<Hackathon[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        let isMounted = true;
        const fetchHackathons = async () => {
            try {
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
                const data: Hackathon[] = await response.json();
                if (isMounted) setHackathons(data);
            } catch (err: unknown) {
                if (isMounted) {
                    if (err instanceof Error) setError(err.message);
                    else setError('Si è verificato un errore anomalo');
                }
            } finally { if (isMounted) setLoading(false); }
        };

        void fetchHackathons();

        return () => { isMounted = false; };
    }, []);

    return (
        <div className="container home-container">
            <header className="hero-section">
                <h1>Benvenuto su HackHub</h1>
                <p className="hero-subtitle">
                    La piattaforma completa per organizzare, partecipare e gestire hackathon in modo semplice e veloce.
                </p>
            </header>

            <section className="features-section">
                <h2>Cosa puoi fare su HackHub?</h2>
                <div className="features-grid">
                    <div className="feature-card">
                        <h3>Partecipa ed Esplora</h3>
                        <p>Crea la tua squadra, invita i tuoi compagni e iscriviti agli hackathon disponibili.</p>
                    </div>
                    <div className="feature-card">
                        <h3>Organizza Eventi</h3>
                        <p>Configura i dettagli dell'hackathon, gestisci i posti disponibili e nomina mentori e giudici.</p>
                    </div>
                    <div className="feature-card">
                        <h3>Gestisci il Team</h3>
                        <p>Invia inviti ai membri, gestisci la rosa dei partecipanti e trasferisci i ruoli di leadership.</p>
                    </div>
                </div>
            </section>

            <section className="hackathons-section">
                <h2>Hackathon Pubblici</h2>

                {loading && <div className="loading-message">Caricamento hackathon in corso...</div>}

                {error && <div className="error-message">{error}</div>}

                {!loading && !error && hackathons.length === 0 && (
                    <p className="empty-message">Nessun hackathon presente al momento.</p>
                )}

                {!loading && !error && hackathons.length > 0 && (
                    <div className="hackathon-grid">
                        {hackathons.map((h, index) => (
                            <div key={index} className="hackathon-card">
                                <div className="hackathon-header">
                                    <h3>{h.nome}</h3>
                                    <span className={`badge ${h.stato.toLowerCase()}`}>{h.stato}</span>
                                </div>
                                <p><strong>Luogo:</strong> {h.luogo}</p>
                                <div><strong>Premio:</strong> {h.premio}€</div>
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
                                <div className="hackathon-footer">
                                    <Link to={isAuthenticated ? "/dashboard" : "/login"} className="btn-primary">
                                        {isAuthenticated ? "Vai alla Dashboard" : "Iscrivi il tuo team"}
                                    </Link>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </section>

            <footer className="home-actions">
                {isAuthenticated ? (
                    <Link to="/dashboard" className="btn-primary">
                        Vai alla tua Dashboard
                    </Link>
                ) : (
                    <Link to="/login" className="btn-primary">
                        Inizia Ora
                    </Link>
                )}
            </footer>
        </div>
    );
}