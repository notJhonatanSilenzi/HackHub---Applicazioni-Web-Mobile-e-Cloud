package unicam.cs.hackhub.domain.implementazione;

import unicam.cs.hackhub.domain.RuoloTeam;
import unicam.cs.hackhub.domain.StatoRichiesta;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("INVITO_TEAM")
public class InvitoTeam extends Richiesta {

    @ManyToOne
    @JoinColumn(name = "team_id_team")
    private Team team;

    public void setTeam(Team team) {
        this.team = team;
    }

    public InvitoTeam() {}

    /**
     * Costruttore che inizializza un invito a entrare in un team
     *
     * @param nomeMittente il nome del mittente
     * @param payload      il messaggio
     * @param destinatario il destinatario
     * @param scadenza     la scadenza dell'invito
     * @param team         il team
     */
    public InvitoTeam(String nomeMittente, String payload, Utente destinatario, LocalDateTime scadenza, Team team) {
        super(nomeMittente, payload, destinatario, scadenza);
        this.team = team;
    }

    @Override
    public void accetta() {
        this.setStato(StatoRichiesta.ACCETTATO);
        MembroTeam membro = new MembroTeam(this.getDestinatario(), team, RuoloTeam.MEMBRO);
        team.aggiungiMembro(membro);
    }

    public Team getTeam() {
        return team;
    }
}
