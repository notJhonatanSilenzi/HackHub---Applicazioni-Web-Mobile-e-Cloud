/**
 * View per la schermata di login/sign in
 */

import { type SubmitEvent, useState} from "react";
import { login, register } from '../api/auth';

export default function AuthView() {
    const [isLogin, setIsLogin] = useState(true); // Login
    const [nomeUtente, setNomeUtente] = useState(''); // Nome Utente
    const [email, setEmail] = useState(''); // Email
    const [password, setPassword] = useState(''); // Password
    const [error, setError] = useState(''); // Messaggio di errore
    const [loading, setLoading] = useState(false); // Caricamento in corso

    const handleSubmit = async (e: SubmitEvent) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            if (isLogin) { // Si sta effettuando un login
                await login({nomeUtente, password});
                alert("Login effettuato con successo!");
                window.location.href = "/dashboard";
            } else { // Si sta effettuando una registrazione
                await register({nomeUtente, email, password});
                alert("Registrazione completata! Ora puoi accedere.");
                setIsLogin(true);
            }
        } catch (err: any) {
            setError(err.message || "Si è verificato un errore!");
        } finally { setLoading(false); }
    };

    return (
        <>
            <div className={"auth-container"}>
                <h2>{isLogin ? 'Accedi ad HackHub' : 'Registrati su HackHub'}</h2>
                {error && <div className={"error-message"}>{error}</div>}
            </div>
            <form onSubmit={handleSubmit}>
                <div className={"form-group"}>
                    <label htmlFor="nomeUtente">Nome Utente:</label>
                    <input id={"nomeUtente"} type={"text"} required={true} value={nomeUtente} onChange={(e) => setNomeUtente(e.target.value)} />
                </div>
                {!isLogin && (
                    <div className={"form-group"}>
                        <label htmlFor={"email"}>Email:</label>
                        <input id={"email"} type={"email"} required={true} value={email} onChange={(e) => setEmail(e.target.value)} />
                    </div>
                )}
                <div className={"form-group"}>
                    <label htmlFor={"password"}>Password:</label>
                    <input id={"password"} type={"password"} required={true} value={password} onChange={(e) => setPassword(e.target.value)} />
                </div>
            </form>
            <button type={"button"} onClick={() => setLoading(!isLogin)} className={"btn-toggle"}>
                {loading ? "Caricamento..." : isLogin ? 'Non hai un account? Registrati!' : 'Hai già un account? Accedi'}
            </button>
        </>
    )
}