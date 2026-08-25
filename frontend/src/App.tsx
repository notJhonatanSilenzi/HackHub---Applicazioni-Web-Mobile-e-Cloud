/**
 * Questa è la sezione di configurazione della navigazione tra viste, della configurazione
 * delle rotte Browser, Routes e Route
 */

import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import AuthView from "./views/AuthView.tsx";
import HomeView from './views/HomeView';
import './index.css'

// Componente temporaneo per la Dashboard
function DashboardView() {
    return (
        <div className="container">
            <h1 className={"title"}>Dashboard Riservata</h1>
            <p>Area di gestione hackathon, team o notifiche / richieste</p>
            <button onClick={() => {
                localStorage.removeItem('token');
                window.location.href="/login";
            }}>Logout</button>
        </div>
    );
}

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
                </Routes>
            </main>
        </BrowserRouter>
    );
}