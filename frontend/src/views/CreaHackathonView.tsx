/**
 * Vista dell'area riservata che permette di creare un hackathon
 */

import React, {type SyntheticEvent, useState} from "react";
import { Link, useNavigate } from "react-router-dom";

/**
 * Funzione che ritorna la vista con il form da compilare per la creazione di un hackathon
 * @constructor
 */
export default function CreaHackathonView() {
    // Definisco le costanti
    const navigate = useNavigate();
    const [formData, setFormData] = useState({ // Stato iniziale del form
        nome: "",
        dataInizio: "",
        dataFine: "",
        luogo: "",
        premio: 0,
        teamMin: 3,
        teamMax: 6,
        regolamento: "",
        scadenzaIscrizioni: "",
        maxIscrizioni: 10
    });
    const [loading, setLoading] = useState(false); // Caricamento in corso
    const [error, setError] = useState<string | null>(null); // Eventuale errore
    const [successMessage, setSuccessMessage] = useState<string | null>(null); // Messaggio di successo

    const [mentori, setMentori] = useState<string[]>([""]); // Insieme dei mentori
    const [giudice, setGiudice] = useState<string>(""); // Giudice

    // Aggiorna il nome del mentore a un determinato indice
    const handleMentoreChange = (index: number, value: string) => {
        const nuoviMentori = [...mentori];
        nuoviMentori[index] = value;
        setMentori(nuoviMentori);
    };

    // Aggiunge un nuovo campo vuoto per un nuovo mentore
    const addMentoreField = () => { setMentori([...mentori, ""]); };

    // Rimuove un campo mentore, se ce n'è più di uno
    const removeMentoreField = (index: number) => {
        if (mentori.length > 1) setMentori(mentori.filter((_, i) => i !== index));
    };

    // Rilevamento dei cambiamenti nel form
    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value, type } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: type === "number" ? Number(value) : value,
        }));
    };

    // Gestione dell'invio dei dati inseriti nel form
    const handleSubmit = async (e: SyntheticEvent) => {
        e.preventDefault(); // Prevenzione di race conditions su onClick e onSubmit
        setLoading(true);
        setError(null);
        setSuccessMessage(null);

        const token = localStorage.getItem("token"); // Preleva il token

        if (!token) { // Nessun token trovato
            setError("Sessione scaduta o utente non autenticato. Effettua nuovamente l'accesso.");
            setLoading(false);
            return;
        }

        try { // Altrimenti effettua la fetch per creare un hackathon
            const response = await fetch("/api/hackathon", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "application/json",
                    "Authorization": `Bearer ${token}`,
                },
                body: JSON.stringify({
                    ...formData,
                    nomeGiudice: giudice.trim(),
                    nomeMentori: mentori.map((mentore) => mentore.trim()).filter(Boolean),
                }),
            });
            if (!response.ok) { // Fetch non andata a buon fine
                if (response.status === 403) throw new Error("Non hai i permessi per creare l'hackathon o la sessione è scaduta");
                throw new Error((await response.text()) || `Errore del server (Codice ${response.status})`);
            }
            // Altrimenti la fetch è andata a buon fine
            setSuccessMessage("Hackathon creato con successo!");
            setTimeout(() => { navigate("/dashboard"); }, 1500);
        } catch (err: unknown) { // Segnalazione di un errore
            if (err instanceof Error) setError(err.message);
            else setError("Si è verificato un errore durante la creazione dell'hackathon")
        }
        finally { setLoading(false); } // Fine del caricamento
    };

    return (
        <>
            <header className="page-header">
                <div className="header-content">
                    <h1 className="page-title">Crea un Nuovo Hackathon</h1>
                </div>
            </header>
            <div className="page">
                {error && <div className="message" data-kind="error">{error}</div>}
                {successMessage && <div className="message" data-kind="success">{successMessage}</div>}

                <form onSubmit={handleSubmit} className="form">
                    <div className="form-field">
                        <label className="form-label" htmlFor="nome">Nome Hackathon *</label>
                        <input className="form-input" type="text" id="nome" name="nome" value={formData.nome} onChange={handleChange} required/>
                    </div>

                    <div className="form-field">
                        <label className="form-label" htmlFor="luogo">Luogo *</label>
                        <input className="form-input" type="text" id="luogo" name="luogo" placeholder="Es. Online / Camerino" value={formData.luogo} onChange={handleChange} required/>
                    </div>

                    <div className="form-row">
                        <div className="form-field">
                            <label className="form-label" htmlFor="dataInizio">Data Inizio *</label>
                            <input className="form-input" type="datetime" id="dataInizio" name="dataInizio" value={formData.dataInizio} onChange={handleChange} required/>
                        </div>

                        <div className="form-field">
                            <label className="form-label" htmlFor="dataFine">Data Fine *</label>
                            <input className="form-input" type="datetime" id="dataFine" name="dataFine" value={formData.dataFine} onChange={handleChange} required/>
                        </div>
                    </div>

                    <div className="form-row">
                        <div className="form-field">
                            <label className="form-label" htmlFor="scadenzaIscrizioni">Scadenza Iscrizioni *</label>
                            <input className="form-input" type="datetime-local" id="scadenzaIscrizioni" name="scadenzaIscrizioni" value={formData.scadenzaIscrizioni} onChange={handleChange} required/>
                        </div>

                        <div className="form-field">
                            <label className="form-label" htmlFor="premio">Premio (€)</label>
                            <input className="form-input" type="number" id="premio" name="premio" min="0" value={formData.premio} onChange={handleChange}/>
                        </div>
                    </div>

                    <div className="form-row">
                        <div className="form-field">
                            <label className="form-label" htmlFor="teamMin">Membri Minimi per Team *</label>
                            <input className="form-input" type="number" id="teamMin" name="teamMin" min="1" value={formData.teamMin} onChange={handleChange} required/>
                        </div>

                        <div className="form-field">
                            <label className="form-label" htmlFor="teamMax">Membri Massimi per Team *</label>
                            <input className="form-input" type="number" id="teamMax" name="teamMax" min="1" value={formData.teamMax} onChange={handleChange} required/>
                        </div>
                    </div>

                    <div className="form-row">
                        <div className="form-field">
                            <label className="form-label" htmlFor="maxIscrizioni">Numero Massimo Team Iscrivibili *</label>
                            <input className="form-input" type="number" id="maxIscrizioni" name="maxIscrizioni" min="1" value={formData.maxIscrizioni} onChange={handleChange} required/>
                        </div>
                    </div>

                    <div className="form-field">
                        <label className="form-label" htmlFor="regolamento">Regolamento</label>
                        <textarea className="form-input" id="regolamento" name="regolamento" rows={4} placeholder="Inserisci il regolamento o le istruzioni dell'hackathon..." value={formData.regolamento} onChange={handleChange}/>
                    </div>

                    <div className="form-field">
                        <label className="form-label" htmlFor="giudice">Username Giudice *</label>
                        <input className="form-input" id="giudice" type="text" value={giudice} onChange={(e) => setGiudice(e.target.value)} required/>
                    </div>

                    {/* Sezione Mentori Dinamici */}
                    <div className="form-field">
                        <label className="form-label">Username Mentori (Almeno 1) *</label>
                        {mentori.map((mentore, index) => (
                            <div key={index} className="form-row">
                                <input className="form-input" type="text" placeholder={`Username Mentore ${index + 1}`} value={mentore} onChange={(e) => handleMentoreChange(index, e.target.value)} required={index === 0}/>
                                {mentori.length > 1 && (
                                    <button type="button" onClick={() => removeMentoreField(index)} className="button" data-variant="danger">✕</button>
                                )}
                            </div>
                        ))}

                        <button type="button" onClick={addMentoreField} className="button" data-variant="secondary">
                            + Aggiungi un altro mentore
                        </button>
                    </div>

                    <button type="submit" className="button" data-variant="primary" disabled={loading}>
                        {loading ? "Creazione in corso..." : "Crea Hackathon"}
                    </button>
                </form>
            </div>
            <div className="page-actions">
                <Link to="/dashboard" className="button" data-variant="secondary">← Torna alla Dashboard</Link>
            </div>
        </>
    );
}
