/**
 * View dedicata alla pagina principale dell'area riservata, accessibile solo dopo
 * aver effettuato il login
 */

import { Link, useNavigate } from 'react-router-dom';

export default function DashboardView() {
    const navigate = useNavigate();
    const handleLogout = () => {
        localStorage.removeItem("token");
        navigate("/login");
    };

    return (
        <div className="container dashboard-container">
            {/* Header / Navigation Bar */}
            <header className="dashboard-header">
                <div className="logo-section">
                    <h1 className="title">HackHub</h1>
                    <span className="subtitle">Dashboard Riservata</span>
                </div>

                <nav className="dashboard-nav">
                    <Link to="/" className="nav-link">Home</Link>
                    <Link to="/dashboard" className="nav-link active">Dashboard</Link>
                    <button onClick={handleLogout} className="btn-logout">
                        Logout
                    </button>
                </nav>
            </header>

            <main className="dashboard-content">
                <h2>Area di Gestione</h2>
                <p>Seleziona un'operazione per iniziare a gestire le tue attività su HackHub:</p>

                <div className="action-cards-grid">
                    {/* Visualizza Hackathon */}
                    <div className="action-card">
                        <div className="card-icon">📅</div>
                        <h3>Visualizza Hackathon</h3>
                        <p>Esplora la lista completa degli hackathon disponibili e consulta tutti i dettagli.</p>
                        <Link to="/hackathons" className="btn-primary">
                            Visualizza Hackathon
                        </Link>
                    </div>

                    {/* Visualizza Team */}
                    <div className="action-card">
                        <div className="card-icon">👥</div>
                        <h3>Visualizza Profilo</h3>
                        <p>Consulta i tuoi dati.</p>
                        <Link to="/profilo" className="btn-primary">
                            Profilo
                        </Link>
                    </div>

                    {/* Crea Hackathon */}
                    <div className="action-card">
                        <div className="card-icon">🚀</div>
                        <h3>Organizza un Hackathon</h3>
                        <p>Crea un nuovo evento, definisci le date, i premi, il regolamento e i requisiti.</p>
                        <Link to="/crea-hackathon" className="btn-primary">
                            Crea Hackathon
                        </Link>
                    </div>

                    {/* Iscrivi Team */}
                    <div className="action-card">
                        <div className="card-icon">🏆</div>
                        <h3>Iscrivi un Team</h3>
                        <p>Seleziona un team e iscrivilo a uno degli hackathon aperti.</p>
                        <Link to="/iscrivi-team" className="btn-primary">
                            Iscrivi Team
                        </Link>
                    </div>
                </div>
            </main>
        </div>
    );
}