package unicam.cs.hackhub.domain.implementazione;

import jakarta.persistence.*;

import java.util.UUID;

/**
 * Sottomissione creata dai team per un'hackathon
 */
@Entity
@Table(name = "sottomissioni")
public class Sottomissione {

    @Id
    @Column(nullable = false, updatable = false)
    private String idSottomissione;

    @Column(nullable = false)
    private String link;

    @OneToOne
    @JoinColumn(name = "valutazione_id_valutazione")
    private Valutazione valutazione;

    public Sottomissione() {
    }

    /**
     * Creazine di una nuova sottomissione di un team
     *
     * @param link il file allegato
     */
    public Sottomissione(String link) {
        this.link = link;
    }

    @PrePersist
    private void assegnaId() {
        if (this.idSottomissione == null) this.idSottomissione = "S-" + UUID.randomUUID();
    }

    /**
     * Metodo che assegna una valutazione a questa sottomissione
     *
     * @param valutazione la valutazione da assegnare
     */
    public void impostaValutazione(Valutazione valutazione) {
        this.valutazione = valutazione;
    }

    public String getIdSottomissione() {
        return idSottomissione;
    }

    public String getLink() {
        return link;
    }

    public Valutazione getValutazione() {
        return valutazione;
    }

    public boolean haValutazione() {
        return this.valutazione != null;
    }
}
