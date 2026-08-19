package unicam.cs.hackhub.domain.implementazione;

import unicam.cs.hackhub.domain.RuoloTeam;
import unicam.cs.hackhub.eccezioni.ConflictException;
import unicam.cs.hackhub.eccezioni.ForbiddenException;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Un Team registrato nella piattaforma, di cui fanno parte un gruppo di Utenti, di cui uno è il Leader,
 * ovvero l'Utente che ha creato il Team.
 */
@Entity
@Table(name = "team")
public class Team {

    @Id
    @Column(nullable = false, updatable = false)
    private String idTeam;

    @Column(nullable = false, unique = true)
    private String nome;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MembroTeam> membri;

    public Team() {
    }

    /**
     * Metodo che crea un nuovo Team.
     *
     * @param nome il nome del Team
     */
    public Team(String nome) {
        this.nome = nome;
        this.membri = new ArrayList<>();
    }

    @PrePersist
    private void assegnaId() {
        if (this.idTeam == null) {
            this.idTeam = "T-" + UUID.randomUUID();
        }
    }


    /**
     * Metodo che aggiunge un membro a questo Team
     *
     * @param membro il membro da aggiungere
     * @throws ConflictException se si tenta di aggiungere un membro con ruolo Leader
     */
    public void aggiungiMembro(MembroTeam membro) {
        if (membro.getRuolo().equals(RuoloTeam.LEADER))
            throw new ConflictException("Tentativo di aggiungere un Leader a un Team");
        membri.add(membro);
        membro.setTeam(this);
    }

    public void rimuoviMembro(MembroTeam membro) {
        membri.remove(membro);
        membro.setTeam(null);
    }

    public void setLeader(MembroTeam membro) throws ForbiddenException {
        if (this.hasLeader()) throw new ForbiddenException("Il team ha già un leader");
        membri.add(membro);
        membro.setRuolo(RuoloTeam.LEADER);
    }

    private boolean hasLeader() {
        for (MembroTeam m : membri)
            if (m.getRuolo() == RuoloTeam.LEADER) return true;
        return false;
    }

    public int getNumMembri() {
        return this.membri.size();
    }

    public String getNome() {
        return this.nome;
    }

    public String getIdTeam() {
        return idTeam;
    }

    public List<MembroTeam> getMembri() {
        return membri;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
