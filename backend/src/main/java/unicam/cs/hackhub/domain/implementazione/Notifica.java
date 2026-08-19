package unicam.cs.hackhub.domain.implementazione;

import unicam.cs.hackhub.domain.TipoNotifica;
import jakarta.persistence.*;

import java.util.UUID;

/**
 * Classe che gestisce le notifiche da inviare alla fine dell'hackathon
 */
@Entity
@Table(name = "notifica")
public class Notifica {

    @Id
    private String idNotifica;
    @ManyToOne(optional = false)
    @JoinColumn(name = "destinatario_id_utente", nullable = false)
    private Utente destinatario;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoNotifica tipoNotifica;
    @Column(nullable = false)
    private String payload;

    public Notifica() {}

    /**
     * Creazione di una notifica
     *
     * @param payload      il payload associato
     * @param destinatario i destinatari
     * @param tipoNotifica il tipo di notifica
     */
    public Notifica(String payload, Utente destinatario, TipoNotifica tipoNotifica) {
        this.destinatario = destinatario;
        this.tipoNotifica = tipoNotifica;
        this.payload = payload;
    }

    /**
     * Assegna un id univoco ad ogni notifica
     */
    @PrePersist
    private void assegnaId() {
        if (this.idNotifica == null) {
            this.idNotifica = "N-" + UUID.randomUUID();
        }
    }

    public String getIdNotifica() {
        return idNotifica;
    }

    public Utente getDestinatario() {
        return destinatario;
    }

    public TipoNotifica getTipoNotifica() {
        return tipoNotifica;
    }

    public String getPayload() {
        return payload;
    }
}
