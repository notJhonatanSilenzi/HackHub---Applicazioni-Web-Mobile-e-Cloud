package unicam.cs.hackhub.domain.implementazione;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * Classe che gestisce l'iscrizione di un team ad un'hackathon
 */
@Entity
@Table(name = "iscrizioneTeam")
public class IscrizioneTeam {

    @Id
    @Column(nullable = false, updatable = false)
    private String idIscrizione;

    @ManyToOne(optional = false)
    @JoinColumn(name = "hackathon_id_hackathon", nullable = false)
    private Hackathon hackathon;

    @ManyToOne(optional = false)
    @JoinColumn(name = "team_id_team", nullable = false)
    private Team team;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sottomissione_id_sottomissione")
    private Sottomissione sottomissione;

    public IscrizioneTeam() {}

    /**
     * Crea un'iscrizione di un team
     *
     * @param team      il team associato all'iscrizione
     * @param hackathon l'hackathon a cui è associata l'iscrizione
     */
    public IscrizioneTeam(Team team, Hackathon hackathon) {
        this.team = team;
        this.hackathon = hackathon;
    }

    /**
     * Assegna un id univoco ad ogni iscrizione
     */
    @PrePersist
    private void assegnaId() {
        if (this.idIscrizione == null) {
            this.idIscrizione = "I-" + UUID.randomUUID();
        }
    }

    /**
     * Metodo che inserisce una nuova sottomissione, se non è presente
     *
     * @param sottomissione la sottomissione da allegare a questa iscrizione
     */
    public void aggiungiSottomissione(Sottomissione sottomissione) {
        if (this.sottomissione != null)
            throw new IllegalStateException("Sottomissione già presente per questa iscrizione.");
        this.sottomissione = sottomissione;
    }

    public void rimuoviSottomissione() {
        this.sottomissione = null;
    }

    public String getId() {
        return idIscrizione;
    }

    public Team getTeam() {
        return team;
    }

    public Hackathon getHackathon() {
        return hackathon;
    }

    public Sottomissione getSottomissione() {
        return sottomissione;
    }

    public void setHackathon(Hackathon hackathon) {
        this.hackathon = hackathon;
    }
}
