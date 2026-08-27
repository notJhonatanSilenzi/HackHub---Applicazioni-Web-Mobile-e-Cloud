/**
 * View per la schermata di login e di registrazione
 */

import {type SyntheticEvent, useState} from "react";
import { login, register } from '../api/auth';

/**
 * Funzione che ritorna la pagina di Login/Registrazione
 * @constructor
 */
export default function AuthView() {
    const [isLogin, setIsLogin] = useState(true); // Distinzione tra login e registrazione
    const [nomeUtente, setNomeUtente] = useState(""); // Casella del nome utente
    const [email, setEmail] = useState(""); // Casella dell'email
    const [password, setPassword] = useState(""); // Casella della password
    const [error, setError] = useState(""); // Eventuale messaggio di errore
    const [loading, setLoading] = useState(false); // Caricamento in corso

    const handleSubmit = async (e: SyntheticEvent) => {
        e.preventDefault(); // Blocca il comportamento predefinito del browser per i casi onClick e onSubmit
        setError("");
        setLoading(true);

        try {
            if (isLogin) { // Caso di Login
                await login({ nomeUtente, password });
                alert("Login effettuato con successo!");
                window.location.href = "/dashboard";
            } else { // Caso di registrazione
                await register({ nomeUtente, email, password });
                alert("Registrazione completata! Ora puoi accedere.");
                setIsLogin(true);
                setPassword("");
            }
        } catch (err: any) { setError(err.message || "Si è verificato un errore!"); } // Gestione errore
        finally { setLoading(false); } // Caricamento concluso
    };

    return (
        <div className="page">
            <h2 className="page-title">{ isLogin ? "Accedi ad HackHub" : "Registrati su HackHub" }</h2>

            {error && <div className="message" data-kind="error">{error}</div>}

            <form onSubmit={handleSubmit} className="form">
                <div className="form-field">
                    <label className="form-label" htmlFor="nomeUtente">Nome Utente:</label>
                    <input className="form-input" id="nomeUtente" type="text" required value={nomeUtente} onChange={(e) => setNomeUtente(e.target.value)}/>
                </div>

                {!isLogin && (
                    <div className="form-field">
                        <label className="form-label" htmlFor="email">Email:</label>
                        <input className="form-input" id="email" type="email" required value={email} onChange={(e) => setEmail(e.target.value)}/>
                    </div>
                )}

                <div className="form-field">
                    <label className="form-label" htmlFor="password">Password:</label>
                    <input className="form-input" id="password" type="password" required value={password} onChange={(e) => setPassword(e.target.value)}/>
                </div>

                <button type="submit" className="button" data-variant="primary" disabled={loading}>
                    { loading ? "Caricamento..." : isLogin ? "Accedi" : "Registrati" }
                </button>
            </form>

            <button type="button" onClick={() => { setIsLogin(!isLogin); setError(''); }} className="button" data-variant="secondary" disabled={loading}>
                { isLogin ? "Non hai un account? Registrati!" : "Hai già un account? Accedi" }
            </button>
        </div>
    );
}
