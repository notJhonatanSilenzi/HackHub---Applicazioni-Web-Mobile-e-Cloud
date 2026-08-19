package unicam.cs.hackhub.domain.implementazione;

import unicam.cs.hackhub.domain.StatoRichiesta;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Classe che gestisce gli elementi generali di una richiesta
 */
@Entity
@Table(name = "richiesta")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_richiesta")
public abstract class Richiesta {

    @Id
    private String idRichiesta;
    @Column(nullable = false)
    private String nomeMittente;
    @ManyToOne(optional = false)
    @JoinColumn(name = "destinatario_id_utente", nullable = false)
    private Utente destinatario;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatoRichiesta stato;
    @Column(nullable = false)
    private String payload;
    @Column(nullable = false)
    private LocalDateTime scadenza;

    public Richiesta() {
    }

    /**
     * Creazione di una nuova richiesta
     *
     * @param nomeMittente il mittente della richiesta
     */
    public Richiesta(String nomeMittente, String payload, Utente destinatario, LocalDateTime scadenza) {
        this.nomeMittente = nomeMittente;
        this.destinatario = destinatario;
        this.stato = StatoRichiesta.INVIATO; //all'inizio quando ancora la richiesta non è stata valutata lo stato è sempre inviato
        this.payload = payload;
        this.scadenza = scadenza;
    }

    /**
     * Assegna un id univoco ad ogni richiesta
     */
    @PrePersist
    private void assegnaId() {
        if (this.idRichiesta == null) {
            this.idRichiesta = "R-" + UUID.randomUUID();
        }
    }

    /**
     * Metodo vuoto che consente di accettare quanto indicato nella richiesta
     */
    public void accetta() {
        this.setStato(StatoRichiesta.ACCETTATO);
    }

    /**
     * Metodo vuoto che consente di rifiutare quando indicato nella richiesta
     */
    public void rifiuta() {
        this.setStato(StatoRichiesta.RIFIUTATO);
    }


    public String getIdRichiesta() {
        return idRichiesta;
    }

    public String getMittente() {
        return nomeMittente;
    }

    public Utente getDestinatario() {
        return destinatario;
    }

    public StatoRichiesta getStato() {
        return stato;
    }

    public String getPayload() {
        return payload;
    }

    public LocalDateTime getScadenza() {
        return scadenza;
    }

    public void setStato(StatoRichiesta stato) {
        this.stato = stato;
    }
}
