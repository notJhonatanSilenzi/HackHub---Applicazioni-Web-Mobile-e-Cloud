package unicam.cs.hackhub.boundary;

import unicam.cs.hackhub.boundary.dto.ValutazioneRequest;
import unicam.cs.hackhub.handler.ValutazioneHandler;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/sottomissioni")
public class ValutazioneBoundary {
    private final ValutazioneHandler handler;

    public ValutazioneBoundary(ValutazioneHandler handler) {
        this.handler = handler;
    }

    /**
     * Inserisce una valutazione in una sottomissione
     * @param idSottomissione l'id della sottomissione
     * @param request il dto con i dati della valutazione
     * @param nomeUtente il nome dell'utente che sta inserendo la valutazione
     * @return una risposta http se la valutazione è stata inserita correttamente
     */
    @PostMapping("{idSottomissione}/valutazione")
    public ResponseEntity<Void> inserisciValutazione(
            @PathVariable String idSottomissione,
            @Valid @RequestBody ValutazioneRequest request,
            @AuthenticationPrincipal String nomeUtente
    ) {
        handler.avviaInserimentoValutazione(idSottomissione, nomeUtente, request);
        return ResponseEntity.noContent().build();
    }
}
