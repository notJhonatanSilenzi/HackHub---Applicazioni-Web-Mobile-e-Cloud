import { Link } from "react-router-dom";
import type { Hackathon } from "../api/hackathonService";

interface HackathonGridProps {
    hackathons: Hackathon[];
    isAuthenticated: boolean;
}

export default function HackathonGrid({ hackathons, isAuthenticated }: HackathonGridProps) {
    if (hackathons.length === 0) {
        return <p className="empty-message">Nessun hackathon presente al momento.</p>;
    }

    return (
        <div className="hackathon-grid">
            {hackathons.map((h, index) => (
                <div key={index} className="hackathon-card">
                    <div className="hackathon-header">
                        <h3>{h.nome}</h3>
                        <span className={`badge ${h.stato.replaceAll("_", " ").toLowerCase()}`}>{h.stato}</span>
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
    );
}