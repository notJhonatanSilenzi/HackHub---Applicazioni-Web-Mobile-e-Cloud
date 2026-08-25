/**
 * Vista dell'area riservata che permette di creare un hackathon
 */

import React, {type SyntheticEvent, useState} from "react";
import { Link, useNavigate } from "react-router-dom";

export default function CreaHackathonView() {
    // Definisco le costanti
    const navigate = useNavigate();
    const [formData, setFormData] = useState({ // Stato iniziale del form
        nome: "",
        dataInizio: "",
        dataFine: "",
        luogo: "",
        premio: 0,
        teamMin: 2,
        teamMax: 5,
        regolamento: "",
        scadenzaIscrizioni: "",
        maxIscrizioni: 10
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [successMessage, setSuccessMessage] = useState<string | null>(null);

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
        e.preventDefault();
        setLoading(true);
        setError(null);
        setSuccessMessage(null);

        const token = localStorage.getItem("token");

        if (!token) {
            setError("Sessione scaduta o utente non autenticato. Effettua nuovamente l'accesso.");
            setLoading(false);
            return;
        }

        try {
            const response = await fetch("/api/hackathon", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "application/json",
                    "Authorization": `Bearer ${token}`,
                }
            });
            if (!response.ok) {
                if (response.status === 403) throw new Error("Non hai i permessi per creare l'hackathon o la sessione è scaduta");
                throw new Error((await response.text()) || `Errore del server (Codice ${response.status})`);
            }

            setSuccessMessage("Hackathon creato con successo!");
            setTimeout(() => { navigate("/dashboard"); }, 1500);
        } catch (err: unknown) {
            if (err instanceof Error) setError(err.message);
            else setError("Si è verificato un errore durante la creazione dell'hackathon")
        }
        finally { setLoading(false); }
    };

    return (
        <div className="container form-container">
            <header className="page-header">
                <Link to="/dashboard" className="btn-back">← Torna alla Dashboard</Link>
                <h1>Crea un Nuovo Hackathon</h1>
            </header>

            {error && <div className="error-message">{error}</div>}
            {successMessage && <div className="success-message">{successMessage}</div>}

            <form onSubmit={handleSubmit} className="hackathon-form">
                <div className="form-group">
                    <label htmlFor="nome">Nome Hackathon *</label>
                    <input type="text" id="nome" name="nome" value={formData.nome} onChange={handleChange} required/>
                </div>

                <div className="form-group">
                    <label htmlFor="luogo">Luogo *</label>
                    <input type="text" id="luogo" name="luogo" placeholder="Es. Online / Camerino" value={formData.luogo} onChange={handleChange} required/>
                </div>

                <div className="form-row">
                    <div className="form-group">
                        <label htmlFor="dataInizio">Data Inizio *</label>
                        <input type="datetime-local" id="dataInizio" name="dataInizio" value={formData.dataInizio} onChange={handleChange} required/>
                    </div>

                    <div className="form-group">
                        <label htmlFor="dataFine">Data Fine *</label>
                        <input type="datetime-local" id="dataFine" name="dataFine" value={formData.dataFine} onChange={handleChange} required/>
                    </div>
                </div>

                <div className="form-row">
                    <div className="form-group">
                        <label htmlFor="scadenzaIscrizioni">Scadenza Iscrizioni *</label>
                        <input type="datetime-local" id="scadenzaIscrizioni" name="scadenzaIscrizioni" value={formData.scadenzaIscrizioni} onChange={handleChange} required/>
                    </div>

                    <div className="form-group">
                        <label htmlFor="premio">Premio (€)</label>
                        <input type="number" id="premio" name="premio" min="0" value={formData.premio} onChange={handleChange}/>
                    </div>
                </div>

                <div className="form-row">
                    <div className="form-group">
                        <label htmlFor="teamMin">Membri Minimi per Team *</label>
                        <input type="number" id="teamMin" name="teamMin" min="1" value={formData.teamMin} onChange={handleChange} required/>
                    </div>

                    <div className="form-group">
                        <label htmlFor="teamMax">Membri Massimi per Team *</label>
                        <input type="number" id="teamMax" name="teamMax" min="1" value={formData.teamMax} onChange={handleChange} required/>
                    </div>

                    <div className="form-group">
                        <label htmlFor="maxIscrizioni">Numero Massimo Team Iscrivibili *</label>
                        <input type="number" id="maxIscrizioni" name="maxIscrizioni" min="1" value={formData.maxIscrizioni} onChange={handleChange} required/>
                    </div>
                </div>

                <div className="form-group">
                    <label htmlFor="regolamento">Regolamento</label>
                    <textarea id="regolamento" name="regolamento" rows={4} placeholder="Inserisci il regolamento o le istruzioni dell'hackathon..." value={formData.regolamento} onChange={handleChange}/>
                </div>

                <button type="submit" className="btn-primary" disabled={loading}>
                    {loading ? "Creazione in corso..." : "Crea Hackathon"}
                </button>
            </form>
        </div>
    );
}