package unicam.cs.hackhub.boundary;

import unicam.cs.hackhub.handler.GestisciRichiesteHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/richieste")
public class GestisciRichiesteBoundary {

    private final GestisciRichiesteHandler handler;

    public GestisciRichiesteBoundary(GestisciRichiesteHandler handler) { this.handler = handler; }

    /**
     * Metodo del boundary che accetta una richiesta di invito Staff, Team o una propostaCall
     * @param idRichiesta l'identificativo della richiesta
     * @return una nuova risposta accettata per lo staff
     */
    @PostMapping("/{idRichiesta}/accetta")
    public ResponseEntity<Void> accettaRichiesta(
            @AuthenticationPrincipal String nomeUtente,
            @PathVariable String idRichiesta
    ) {
        handler.accettaRichiesta(nomeUtente, idRichiesta);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    /**
     * Metodo del boundary che rifiuta una richiesta di invito Staff, Team o una proposta di call
     * @param idRichiesta l'identificativo della richiesta
     * @return una nuova risposta rifiutata per lo staff
     */
    @PostMapping("/{idRichiesta}/rifiuta")
    public ResponseEntity<Void> rifiutaRichiesta(
            @AuthenticationPrincipal String nomeUtente,
            @PathVariable String idRichiesta
    ) {
        handler.rifiutaRichiesta(nomeUtente, idRichiesta);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
