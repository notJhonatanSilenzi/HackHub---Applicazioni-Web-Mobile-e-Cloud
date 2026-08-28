/**
 * Vista in cui si può visualizzare l'elenco di tutti gli hackathon
 */

import { Link } from "react-router-dom";
import HackathonGrid from "../views/HackathonGrid";
import { useHackathons } from "../api/useHackathons.ts";

/**
 * Funzione che ritorna la vista sulla lista di hackathon, distinguendo tra il caso
 * di utente autenticato e il caso di visitatore
 * @constructor
 */
export default function ListaHackathonDashboardView() {
    const { hackathons, loading, error, isAuthenticated } = useHackathons();

    // Gestione dinamica di destinazione e testo in base all'autenticazione
    const backToPath = isAuthenticated ? "/dashboard" : "/login";
    const backToLabel = isAuthenticated ? "← Torna alla Dashboard" : "← Torna alla Home";

    return (
        <>
            <header className="page-header">
                <div className="header-content">
                    <h1 className="page-title">Tutti gli Hackathon Disponibili</h1>
                </div>
            </header>
            <div className="page">
                {loading && <div className="message" data-kind="loading">Caricamento hackathon in corso...</div>}
                {error && <div className="message" data-kind="error">{error}</div>}

                {!loading && !error && (
                    <HackathonGrid hackathons={hackathons} isAuthenticated={isAuthenticated} />
                )}
                <div className="page-actions">
                    <Link to={backToPath} className="button" data-variant="secondary">
                        {backToLabel}
                    </Link>
                </div>
            </div>
        </>
    );
}
