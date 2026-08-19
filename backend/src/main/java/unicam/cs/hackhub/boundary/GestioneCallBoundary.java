package unicam.cs.hackhub.boundary;

import unicam.cs.hackhub.boundary.dto.PropostaCallRequest;
import unicam.cs.hackhub.handler.GestioneCallHandler;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/call")
public class GestioneCallBoundary {

    private final GestioneCallHandler handler;

    public GestioneCallBoundary(GestioneCallHandler handler) {
        this.handler = handler;
    }

    /**
     * Avvia una nuova proposta di call
     * @param nomeUtente l'utente che manda la proposta
     * @param request la richiesta della proposta con i dati
     * @return una nuova chiamata http
     */
    @PostMapping("/proposta")
    public ResponseEntity<Void> avviaPropostaCall(
            @AuthenticationPrincipal String nomeUtente,
            @RequestBody @Valid PropostaCallRequest request
            ){
              handler.avviaPropostaCall(nomeUtente, request);
            return ResponseEntity.noContent().build();
    }
}
