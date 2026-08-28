/**
 * View per la sezione di iscrizione dei team a un hackathon
 */

import {useState, useEffect, type SyntheticEvent} from "react";
import {Link, useNavigate, useParams} from "react-router-dom";

interface Hackathon {
    id: number;
    nome: string;
    scadenzaIscrizioni: string;
}

/**
 * Funzione che ritorna la vista sul form di iscrizione di un team a un hackathon selezionato
 * @constructor
 */
export default function IscriviTeamView() {
    // 1. Recupera l'id dall'URL (se presente, es. /iscrivi-team/123)
    const { nome } = useParams<{ nome: string }>();

    const [hackathons, setHackathons] = useState<Hackathon[]>([]);
    const [selectedHackathonNome, setSelectedHackathonNome] = useState<string>("");
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [message, setMessage] = useState<{ text: string; isError: boolean } | null>(null);

    const nav = useNavigate();

    useEffect(() => {
        let isMounted = true;
        const fetchHackathons = async () => {
            const token = localStorage.getItem("token");
            try {
                const resp = await fetch("/api/hackathon", {
                    method: "GET",
                    headers: {
                        "Accept": "application/json",
                        ...(token ? { "Authorization": `Bearer ${token}` } : {})
                    }
                });
                if (resp.ok) {
                    const data: Hackathon[] = await resp.json();
                    if (isMounted) setHackathons(data);
                } else if (isMounted) setMessage({ text: "Impossibile caricare gli hackathon.", isError: true });

                // 2. Se c'è un ID nell'URL, lo imposta come valore selezionato di default
                if (nome) setSelectedHackathonNome(decodeURIComponent(nome));
            } catch (err: unknown) { if (isMounted) setMessage({ text: "Errore di connessione al server.", isError: true }); }
            finally { if (isMounted) setLoading(false); }
        }
        void fetchHackathons();
        return () => { isMounted = false; };
    }, []);

    const handleSubmit = async (e: SyntheticEvent) => {
        e.preventDefault();
        if (!selectedHackathonNome) {
            setMessage({ text: "Seleziona un hackathon dalla lista.", isError: true });
            return;
        }
        setSubmitting(true);
        setMessage(null);

        const token = localStorage.getItem("token");
        try {
            const response = await fetch(`/api/hackathon/${selectedHackathonNome}/iscrizioni`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`,
                }
            });
            if (response.ok) {
                setMessage({ text: "Iscrizione del team completata con successo!", isError: false });
                setTimeout(() => nav("/dashboard"), 2000);
            } else {
                const errData = await response.json().catch(() => null);
                const errorText = errData?.message || `Errore durante l'iscrizione (Codice ${response.status})`;
                setMessage({ text: errorText, isError: true });
            }
        } catch (err: unknown) { setMessage({ text: "Errore di rete durante l'iscrizione.", isError: true }); }
        finally { setSubmitting(false); }
    };

    return (
        <>
            <header className="page-header">
                <div className="header-content">
                    <h1 className="page-title">Iscrivi il tuo Team ad un Hackathon</h1>
                </div>
            </header>
            <div className="page">
                {message && (
                    <div className="message" data-kind={message.isError ? "error" : "success"}>
                        {message.text}
                    </div>
                )}

                {loading ? (
                    <p>Caricamento hackathon disponibili...</p>
                ) : (
                    <form onSubmit={handleSubmit} className="form">
                        <div className="form-field">
                            <label className="form-label" htmlFor="hackathon-select">
                                Seleziona Hackathon:
                            </label>
                            <select className="form-input" id="hackathon-select" value={selectedHackathonNome} onChange={(e) => setSelectedHackathonNome(e.target.value)} disabled={submitting} required>
                                <option value="">-- Scegli un hackathon --</option>
                                {hackathons.map((h) => (
                                    <option key={h.nome} value={h.nome}>
                                        {h.nome}
                                    </option>
                                ))}
                            </select>
                        </div>

                        <button type="submit" className="button" data-variant="primary" disabled={submitting || !selectedHackathonNome}>
                            {submitting ? "Iscrizione in corso..." : "Conferma Iscrizione"}
                        </button>
                    </form>
                )}
                <div className="page-actions">
                    <Link to="/dashboard" className="button" data-variant="secondary">← Torna alla Dashboard</Link>
                </div>
            </div>
        </>
    );
}
