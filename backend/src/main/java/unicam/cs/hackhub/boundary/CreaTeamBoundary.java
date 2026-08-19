package unicam.cs.hackhub.boundary;

import unicam.cs.hackhub.handler.CreaTeamHandler;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/team")
public class CreaTeamBoundary {

    private final CreaTeamHandler handler;

    public CreaTeamBoundary(CreaTeamHandler handler) {
        this.handler = handler;
    }

    /**
     * Metodo che avvia la creazione di un team
     * @param nomeUtente il nome dell'utente che vuole creare il team
     * @param nomeTeam il nome del team
     * @return una nuova chiamata http
     */
    @PostMapping()
    public ResponseEntity<Void> avviaCreazioneTeam(
            @AuthenticationPrincipal String nomeUtente,
            @RequestBody String nomeTeam
            ){
        handler.avviaCreazioneTeam(nomeUtente, nomeTeam);
        return ResponseEntity.noContent().build();
    }
}
