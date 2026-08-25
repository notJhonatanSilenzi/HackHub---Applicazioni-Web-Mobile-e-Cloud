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
import './index.css'

export default function App() {
    return (
        <BrowserRouter>
            <header className="navbar">
                <Link to={"/"} className={"brand"}>
                    <nav>
                        <Link to={"/"}>Home</Link> | <Link to={"/login"}>Login</Link>
                    </nav>
                </Link>
            </header>

            <main>
                <Routes>
                    <Route path="/" element={<HomeView />} />
                    <Route path="/login" element={<AuthView />} />
                    <Route path="/dashboard" element={<DashboardView />} />
                    <Route path="/crea-hackathon" element={<CreaHackathonView />} />
                    <Route path="/profilo" element={<InfoUtenteView />} />                </Routes>
            </main>
        </BrowserRouter>
    );
}