package unicam.cs.hackhub.domain.implementazione;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Transient;

import java.util.UUID;

@Entity
@DiscriminatorValue("CALL_SLOT")
public class CallSlot extends Richiesta{

    @Transient
    private Staff mentore;
    @Transient
    private Team team;

    private String link;
    private Periodo periodo;

    public CallSlot(){}

    /**
     * Crea un nuovo slot nel calendario
     * @param periodo il periodo
     * @param team il team
     * @param mentore il mentore
     * @param link il link
     */
    public CallSlot(Periodo periodo, Team team, Staff mentore, String link){
        this.periodo = periodo;
        this.team = team;
        this.mentore = mentore;
        this.link = link;
    }
    public Team getTeam() {
        return team;
    }
}
