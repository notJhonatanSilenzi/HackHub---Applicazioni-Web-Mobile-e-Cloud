package unicam.cs.hackhub.domain.implementazione;

import unicam.cs.hackhub.domain.RuoloTeam;
import jakarta.persistence.*;

import java.util.UUID;

/**
 * Un utente registrato alla piattaforma che diventa parte di un team
 */
@Entity
@Table(name = "membro_team")
public class MembroTeam {

    @Id
    @Column(nullable = false, updatable = false)
    private String idMembroTeam;

    @OneToOne(optional = false)
    @JoinColumn(name = "utente_id_utente")
    private Utente utente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "team_id_team", nullable = false)
    private Team team;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuoloTeam ruolo;

    public MembroTeam() {}

    /**
     * Creazione di un membro del team
     *
     * @param utente    l'utente che diventa membro del team
     * @param team      il team a cui appartiene
     * @param ruoloTeam il ruolo del membro del team
     */
    public MembroTeam(Utente utente, Team team, RuoloTeam ruoloTeam) {
        this.utente = utente;
        this.team = team;
        this.ruolo = ruoloTeam;
    }

    /**
     * Assegna un id univoco ad ogni membro di un team
     */
    @PrePersist
    private void assegnaId() {
        if (this.idMembroTeam == null) {
            this.idMembroTeam = "MT-" + UUID.randomUUID();
        }
    }

    public String getIdMembroTeam() {
        return idMembroTeam;
    }

    public RuoloTeam getRuolo() {
        return ruolo;
    }

    public Utente getUtente() {
        return utente;
    }

    public Team getTeam() {
        return team;
    }

    public void setRuolo(RuoloTeam r) {
        this.ruolo = r;
    }

    public void setTeam(Team t) {
        this.team = t;
    }
}
