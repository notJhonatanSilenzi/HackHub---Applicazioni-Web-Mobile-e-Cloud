package unicam.cs.hackhub.boundary;

import unicam.cs.hackhub.handler.RispondeRichiesteSupportoHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/richieste-supporto")
@Validated
public class RispondeRichiesteSupportoBoundary {

    private final RispondeRichiesteSupportoHandler handler;

    public RispondeRichiesteSupportoBoundary(RispondeRichiesteSupportoHandler handler) {
        this.handler = handler;
    }

    /**
     * Metodo che ritorna l'esito della chiamata http, e che risponde a una richiesta di supporto con
     * una notifica
     * @param idNotifica l'id della notifica che richiede supporto a un mentore di un hackathon
     * @return l'esito della chiamata http, e salva nel db una proposta call oppure una notifica di risposta
     */
    @PostMapping("risposta")
    public ResponseEntity<Void> rispondiRichiestaSupportoConNotifica(
            @RequestParam String idNotifica,
            @AuthenticationPrincipal String nomeUtente) {
        handler.rispondiRichiestaSupportoConNotifica(nomeUtente, idNotifica);
        return ResponseEntity.ok().build();
    }
}
