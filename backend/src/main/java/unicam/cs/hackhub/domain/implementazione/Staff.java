package unicam.cs.hackhub.domain.implementazione;

import unicam.cs.hackhub.domain.RuoloStaff;
import jakarta.persistence.*;

import java.util.UUID;

/**
 * Classe che gestisce un singolo membro dello staff per un'hackathon e lo associa all'hackathon
 * in cui lavora
 */
@Entity
@Table(name = "staff")
public class Staff {

    @Id
    @Column(nullable = false, updatable = false)
    private String idStaff;

    @ManyToOne(optional = false)
    @JoinColumn(name = "utente_id_utente", nullable = false)
    private Utente utente;

    @ManyToOne
    @JoinColumn(name = "hackathon_id_hackathon")
    private Hackathon hackathon;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuoloStaff ruolo;

    /**
     * Creazione di un membro dello staff
     *
     * @param utente l'utente associato allo staff
     * @param ruolo  il ruolo ricoperto
     */
    public Staff(Utente utente, RuoloStaff ruolo) {
        this.utente = utente;
        this.ruolo = ruolo;
    }

    public Staff() {
    }

    @PrePersist
    private void assegnaId() {
        if (this.idStaff == null) {
            this.idStaff = "MS-" + UUID.randomUUID();
        }
    }

    public RuoloStaff getRuolo() {
        return ruolo;
    }

    public Utente getUtente() {
        return utente;
    }

    public Hackathon getHackathon() {
        return hackathon;
    }

    public void setHackathon(Hackathon hackathon) {
        this.hackathon = hackathon;
    }

    public String getIdStaff() {
        return idStaff;
    }
}
