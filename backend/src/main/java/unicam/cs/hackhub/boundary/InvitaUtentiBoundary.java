package unicam.cs.hackhub.boundary;

import unicam.cs.hackhub.handler.InvitaUtentiHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/team")
public class InvitaUtentiBoundary {

    private final InvitaUtentiHandler handler;

    public InvitaUtentiBoundary(InvitaUtentiHandler handler){
        this.handler = handler;
    }

    /**
     * Metodo della boundary che invita un utente ad un team
     * @param nomeUtente il nome dell'utente che invita
     * @param nomeUtenteDaInvitare l'utente da invitare
     * @return una nuova risposta http
     */
    @PostMapping("/mio/invito")
    public ResponseEntity<Void> InvitaUtenti(@AuthenticationPrincipal String nomeUtente,
                                             @RequestParam String nomeUtenteDaInvitare){
        handler.invitaUtenti(nomeUtente, nomeUtenteDaInvitare);
        return ResponseEntity.noContent().build();
    }
}
