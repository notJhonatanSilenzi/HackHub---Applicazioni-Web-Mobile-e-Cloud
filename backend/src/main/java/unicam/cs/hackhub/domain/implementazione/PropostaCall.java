package unicam.cs.hackhub.domain.implementazione;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.validation.Valid;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("PROPOSTA_CALL")
public class PropostaCall extends Richiesta {

    @Embedded
    @Valid
    private Periodo periodo;

    public PropostaCall() {}

    /**
     * Costruttore che istanzia una Proposta di call
     * @param nomeMittente il nome del mittente
     * @param payload il messaggio
     * @param destinatario il destinatario
     * @param scadenza la scadenza dell'invito
     * @param periodo il periodo
     */
    public PropostaCall(String nomeMittente, String payload, Utente destinatario, LocalDateTime scadenza, Periodo periodo) {
        super(nomeMittente, payload, destinatario, scadenza);
        this.periodo = periodo;
    }

    public Periodo getPeriodo() {
        return periodo;
    }
}
