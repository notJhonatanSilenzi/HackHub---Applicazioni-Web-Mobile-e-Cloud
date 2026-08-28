/**
 * Componente che ritorna la griglia di hackathon, adattata per il frontend
 */

import { Link } from "react-router-dom";
import type { Hackathon } from "../api/hackathonService";

interface HackathonGridProps {
    hackathons: Hackathon[];
    isAuthenticated: boolean;
}

/**
 * Funzione che ritorna in formato accettabile dal frontend la lista/griglia di hackathon
 * @param param0
 * @param param0.hackathons
 * @param param0.isAuthenticated
 * @constructor
 */
export default function HackathonGrid({ hackathons, isAuthenticated }: HackathonGridProps) {
    if (hackathons.length === 0) {
        return <p className="empty-state">Nessun hackathon presente al momento.</p>;
    }

    return (
        <div className="table-wrapper">
            <table className="table">
                <thead>
                <tr>
                    <th>Nome</th>
                    <th>Stato</th>
                    <th>Luogo</th>
                    <th>Premio</th>
                    <th>Date Evento</th>
                    <th>Scadenza Iscrizioni</th>
                    <th>Team (Min-Max)</th>
                    <th>Posti</th>
                    <th>Azione</th>
                </tr>
                </thead>
                <tbody>
                {hackathons.map((h, index) => (
                    <tr key={index}>
                        <td className="table-cell" data-emphasis="strong">{h.nome}</td>
                        <td>
                                <span className="badge" data-status={h.stato.toLowerCase()}>
                                    {h.stato}
                                </span>
                        </td>
                        <td>{h.luogo}</td>
                        <td className="table-cell" data-emphasis="strong">{h.premio}€</td>
                        <td>
                            {new Date(h.dataInizio).toLocaleDateString()} - {new Date(h.dataFine).toLocaleDateString()}
                        </td>
                        <td>{new Date(h.scadenzaIscrizioni).toLocaleDateString()}</td>
                        <td>{h.teamMin} - {h.teamMax} membri</td>
                        <td>{h.postiRimanenti}</td>
                        <td>
                            <Link to={isAuthenticated ? `/iscrivi-team/${encodeURIComponent(h.nome)}` : "/login"} className="button" data-variant="primary">
                                {isAuthenticated ? "Iscriviti" : "Login"}
                            </Link>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}
