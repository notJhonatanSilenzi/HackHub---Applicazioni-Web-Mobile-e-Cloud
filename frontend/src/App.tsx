/**
 * Questa è la sezione di configurazione della navigazione tra viste, della configurazione
 * delle rotte Browser, Routes e Route
 */

import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import AuthView from "./views/AuthView.tsx";
import HomeView from './views/HomeView';
import DashboardView from "./views/DashboardView.tsx";
import CreaHackathonView from "./views/CreaHackathonView.tsx";
import InfoUtenteView from "./views/InfoUtenteView.tsx";
import IscriviTeamView from "./views/IscriviTeamView.tsx";
import HackathonListView from "./views/HackathonListView.tsx";
import './index.css'

export default function App() {
    return (
        <BrowserRouter>
            <header className="navigation-bar" /* Barra di navigazione */>
                <nav className="navigation">
                    <Link to={"/"} className="navigation-link">Home</Link>
                    <Link to={"/login"} className="navigation-link">Login</Link>
                    <Link to={"/hackathons"} className="navigation-link">Vedi Hackathon</Link>
                </nav>
            </header>

            <main>
                <Routes /* Tutte le rotte del frontend */>
                    <Route path="/" element={<HomeView />} />
                    <Route path="/login" element={<AuthView />} />
                    <Route path="/dashboard" element={<DashboardView />} />
                    <Route path="/crea-hackathon" element={<CreaHackathonView />} />
                    <Route path="/profilo" element={<InfoUtenteView />} />
                    <Route path="/iscrivi-team/:nome?" element={<IscriviTeamView />} />
                    <Route path="/hackathons" element={<HackathonListView />} />
                </Routes>
            </main>
        </BrowserRouter>
    );
}
