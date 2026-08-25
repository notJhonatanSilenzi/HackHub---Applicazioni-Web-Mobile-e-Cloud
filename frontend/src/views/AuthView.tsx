/**
 * View per la schermata di login/sign in
 */

import React, { useState } from "react";
import { login, register } from '../api/auth';

export default function AuthView() {
    const [isLogin, setIsLogin] = useState(true);
    const [nomeUtente, setNomeUtente] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            if (isLogin) {
                await login({ nomeUtente, password });
                alert("Login effettuato con successo!");
                window.location.href = "/dashboard";
            } else {
                await register({ nomeUtente, email, password });
                alert("Registrazione completata! Ora puoi accedere.");
                setIsLogin(true);
                setPassword('');
            }
        } catch (err: any) { setError(err.message || "Si è verificato un errore!"); }
        finally { setLoading(false); }
    };

    return (
        <div className="auth-container">
            <h2>{isLogin ? 'Accedi ad HackHub' : 'Registrati su HackHub'}</h2>

            {error && <div className="error-message">{error}</div>}

            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label htmlFor="nomeUtente">Nome Utente:</label>
                    <input
                        id="nomeUtente"
                        type="text"
                        required
                        value={nomeUtente}
                        onChange={(e) => setNomeUtente(e.target.value)}
                    />
                </div>

                {!isLogin && (
                    <div className="form-group">
                        <label htmlFor="email">Email:</label>
                        <input
                            id="email"
                            type="email"
                            required
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                        />
                    </div>
                )}

                <div className="form-group">
                    <label htmlFor="password">Password:</label>
                    <input
                        id="password"
                        type="password"
                        required
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                </div>

                <button type="submit" className="btn-primary" disabled={loading}>
                    {loading ? "Caricamento..." : isLogin ? 'Accedi' : 'Registrati'}
                </button>
            </form>

            <button
                type="button"
                onClick={() => {
                    setIsLogin(!isLogin);
                    setError('');
                }}
                className="btn-toggle"
                disabled={loading}
            >
                {isLogin ? 'Non hai un account? Registrati!' : 'Hai già un account? Accedi'}
            </button>
        </div>
    );
}