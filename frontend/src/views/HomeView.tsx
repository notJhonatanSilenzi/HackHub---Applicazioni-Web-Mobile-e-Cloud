/**
 * Vista della Home principale, con le informazioni principali sulla piattaforma
 */

import { Link } from "react-router-dom";

export default function HomeView() {
    // Verifica se c'è in locale un token ancora valido
    const isAuthenticated = Boolean(localStorage.getItem("token"));

    return (
        <>
            <header className="page-header">
                <div className="header-content">
                    <h1 className="page-title">Benvenuto su HackHub</h1>
                    <p className="page-text">
                        La piattaforma completa per organizzare, partecipare e gestire hackathon in modo semplice e veloce.
                    </p>
                </div>
            </header>
            <main className="page">
                <section className="section">
                    <h2 className="section-title">Cosa puoi fare su HackHub?</h2>
                    <div className="card-list">
                        <div className="card">
                            <h3 className="card-title">Partecipa ed Esplora</h3>
                            <p className="card-text">Crea la tua squadra, invita i tuoi compagni e iscriviti agli hackathon disponibili.</p>
                        </div>
                        <div className="card">
                            <h3 className="card-title">Organizza Eventi</h3>
                            <p className="card-text">Configura i dettagli dell'hackathon, gestisci i posti disponibili e nomina mentori e giudici.</p>
                        </div>
                        <div className="card">
                            <h3 className="card-title">Gestisci il Team</h3>
                            <p className="card-text">Invia inviti ai membri, gestisci la rosa dei partecipanti e trasferisci i ruoli di leadership.</p>
                        </div>
                    </div>
                </section>
                <footer className="page-actions">
                    {isAuthenticated ? ( // Se il token è valido, vai direttamente alla dashboard
                        <Link to="/dashboard" className="button" data-variant="primary">
                            Vai alla tua Dashboard
                        </Link>
                    ) : ( // Altrimenti vai alla sezione di login
                        <Link to="/login" className="button" data-variant="primary">
                            Inizia Ora
                        </Link>
                    )}
                </footer>
            </main>
        </>
    );
}
