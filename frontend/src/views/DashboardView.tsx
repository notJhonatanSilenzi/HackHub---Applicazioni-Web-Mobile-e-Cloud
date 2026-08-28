/**
 * View dedicata alla pagina principale dell'area riservata, accessibile solo dopo
 * aver effettuato il login
 */

import { Link, useNavigate } from "react-router-dom";

/**
 * Funzione che inizializza la vista sulla Dashboard / Area Riservata
 * @constructor
 */
export default function DashboardView() {
    const navigate = useNavigate(); // Usa la navigazione tra viste
    const handleLogout = () => { // Logout
        localStorage.removeItem("token");
        navigate("/login");
    };

    return (
        <>
            <header className="page-header">
                <div className="header-content">
                    <h1 className="page-title">HackHub</h1>
                    <span className="page-subtitle">Dashboard Riservata</span>
                </div>
            </header>
            <div className="page">
                <main className="page-content">
                    <h2 className="section-title">Area di Gestione</h2>
                    <p className="page-text">Seleziona un'operazione per iniziare a gestire le tue attività su HackHub:</p>

                    <div className="card-list">
                        {/* Visualizza Hackathon */}
                        <div className="card">
                            <h3 className="card-title">Visualizza Hackathon</h3>
                            <p className="card-text">Esplora la lista completa degli hackathon disponibili e consulta tutti i dettagli.</p>
                            <Link to="/hackathons" className="button" data-variant="primary">
                                Visualizza Hackathon
                            </Link>
                        </div>

                        {/* Crea Hackathon */}
                        <div className="card">
                            <h3 className="card-title">Organizza un Hackathon</h3>
                            <p className="card-text">Crea un nuovo evento, definisci le date, i premi, il regolamento e i requisiti.</p>
                            <Link to="/crea-hackathon" className="button" data-variant="primary">
                                Crea Hackathon
                            </Link>
                        </div>

                        {/* Iscrivi Team */}
                        <div className="card">
                            <h3 className="card-title">Iscrivi un Team</h3>
                            <p className="card-text">Seleziona un team e iscrivilo a uno degli hackathon aperti.</p>
                            <Link to="/iscrivi-team" className="button" data-variant="primary">
                                Iscrivi Team
                            </Link>
                        </div>

                        {/* Visualizza Team */}
                        <div className="card">
                            <h3 className="card-title">Visualizza Profilo</h3>
                            <p className="card-text">Consulta i tuoi dati: nome utente, email e team di appartenenza.</p>
                            <Link to="/profilo" className="button" data-variant="primary">
                                Profilo
                            </Link>
                        </div>
                    </div>
                    <div className="page-actions">
                        <nav className="navigation">
                            <button onClick={handleLogout} className="button" data-variant="danger">
                                Logout
                            </button>
                        </nav>
                    </div>
                </main>
            </div>
        </>
    );
}
