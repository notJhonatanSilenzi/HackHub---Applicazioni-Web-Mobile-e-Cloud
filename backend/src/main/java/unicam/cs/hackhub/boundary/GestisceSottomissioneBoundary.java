package unicam.cs.hackhub.boundary;

import unicam.cs.hackhub.handler.GestisceSottomissioneHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sottomissioni")
public class GestisceSottomissioneBoundary {

    private final GestisceSottomissioneHandler handler;

    /**
     * Metodo che istanzia la boundary per la gestione delle sottomissioni
     * @param handler l'handler
     */
    public GestisceSottomissioneBoundary(GestisceSottomissioneHandler handler) {
        this.handler = handler;
    }

    /**
     * Invia una sottomissione per un hackathon
     * @param nomeUtente il nome utente del membro del team che la invia
     * @param link il link contenente la sottomissione
     * @return una nuova chiamata http
     */
    @PostMapping("{nomeHackathon}")
    public ResponseEntity<Void> inviaSottomissione(
            @AuthenticationPrincipal String nomeUtente,
            @RequestBody String link,
            @PathVariable String nomeHackathon) {
        handler.inviaSottomissione(nomeUtente, nomeHackathon, link);
        return ResponseEntity.ok().build();
    }

    /**
     * Rimuove una sottomissione inviata
     * @param nomeUtente il nome utente del membro del team che la vuole rimuovere
     * @return una nuova chiamata http
     */
    @DeleteMapping("{nomeHackathon}")
    public ResponseEntity<Void> attivaRimozioneSottomissione(
            @AuthenticationPrincipal String nomeUtente,
            @PathVariable String nomeHackathon) {
        handler.attivaRimozioneSottomissione(nomeUtente, nomeHackathon);
        return ResponseEntity.ok().build();
    }
}
