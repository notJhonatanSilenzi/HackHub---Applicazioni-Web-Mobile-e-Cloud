/**
 * Vista della Home principale
 */

import { Link } from "react-router-dom";
import HackathonGrid from "../views/HackathonGrid";
import {useHackathons} from "../api/useHackathons.ts";

export default function HomeView() {
    const { hackathons, loading, error, isAuthenticated } = useHackathons();

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

                {!loading && !error && (
                    <HackathonGrid hackathons={hackathons} isAuthenticated={isAuthenticated} />
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