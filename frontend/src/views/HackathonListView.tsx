/**
 * Vista della Dashboard in cui si può visualizzare l'elenco di tutti gli hackathon
 */

import { Link } from "react-router-dom";
import HackathonGrid from "../views/HackathonGrid";
import { useHackathons } from "../api/useHackathons.ts";

export default function ListaHackathonDashboardView() {
    const { hackathons, loading, error, isAuthenticated } = useHackathons();

    return (
        <div className="container dashboard-subview">
            <header className="page-header">
                <Link to="/dashboard" className="btn-back">← Torna alla Dashboard</Link>
                <h1>Tutti gli Hackathon Disponibili</h1>
            </header>

            {loading && <div className="loading-message">Caricamento hackathon in corso...</div>}
            {error && <div className="error-message">{error}</div>}

            {!loading && !error && (
                <HackathonGrid hackathons={hackathons} isAuthenticated={isAuthenticated} />
            )}
        </div>
    );
}