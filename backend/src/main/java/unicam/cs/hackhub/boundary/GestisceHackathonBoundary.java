package unicam.cs.hackhub.boundary;

import unicam.cs.hackhub.handler.GestisceHackathonHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hackathon")
public class GestisceHackathonBoundary {

    private final GestisceHackathonHandler handler;

    public GestisceHackathonBoundary(GestisceHackathonHandler handler) {
        this.handler = handler;
    }

    /**
     * Metodo della boundary per segnalare una violazione
     * @param nomeMentore il nome del mentore che segnala la violazione
     * @param nomeTeam il nome del team che ha commesso la violazione
     * @return una nuova chiamata http
     */
    @PostMapping("/{nomeHackathon}/violazione")
    public ResponseEntity<Void> segnalaViolazione(
            @AuthenticationPrincipal String nomeMentore,
            @RequestParam String nomeTeam,
            @PathVariable String nomeHackathon){
        handler.segnalaViolazione(nomeMentore, nomeTeam, nomeHackathon);
        return ResponseEntity.ok().build();
    }

    /**
     * Metodo della boundary per nominare mentori
     * @param nomeUtente il nome dell'organizzatore
     * @param nomeUtenteDaInvitare il nome dell'utente da invitare
     * @return una nuova chiamata http
     */
    @PostMapping("/{nomeHackathon}/nomine-mentori")
    public ResponseEntity<Void> nominaMentori(
            @AuthenticationPrincipal String nomeUtente,
            @RequestParam String nomeUtenteDaInvitare,
            @PathVariable String nomeHackathon){
        handler.nominaMentori(nomeUtente, nomeUtenteDaInvitare, nomeHackathon);
        return ResponseEntity.ok().build();
    }

    /**
     * Metodo della boundary per eliminare un hackathon
     * @param nomeUtente l'organizzatore che lo vuole eliminare
     * @param nomeHackathon l'id dell'hackathon
     * @return una nuova chiamata http
     */
    @DeleteMapping("/{nomeHackathon}")
    public ResponseEntity<Void> eliminaHackathon(
            @AuthenticationPrincipal String nomeUtente,
            @PathVariable String nomeHackathon){
        handler.eliminaHackathon(nomeUtente, nomeHackathon);
        return ResponseEntity.ok().build();
    }

    /**
     * Metodo della boundary per espellere un team da un hackathon
     * @param nomeUtente l'organizzatore che espelle il team
     * @param nomeHackathon il nome dell'hackathon
     * @param nomeTeam il nome del team
     * @return una nuova chiamata http
     */
    @PostMapping("/{nomeHackathon}/team/{nomeTeam}/espulsione")
    public ResponseEntity<Void> espelliTeam(
            @AuthenticationPrincipal String nomeUtente,
            @PathVariable String nomeHackathon,
            @PathVariable String nomeTeam) {
        handler.espelliTeam(nomeUtente, nomeHackathon, nomeTeam);
        return ResponseEntity.ok().build();
    }

    /**
     * Metodo della boundary per proclamare il vincitore di un hackathon
     * @param nomeUtente l'organizzatore che proclama il vincitore
     * @param nomeHackathon il nome dell'hackathon
     * @param nomeTeam il nome del team
     * @return una nuova chiamata http
     */
    @PostMapping("/{nomeHackathon}/vincitore")
    public ResponseEntity<Void> proclamaVincitore(
            @AuthenticationPrincipal String nomeUtente,
            @PathVariable String nomeHackathon,
            @RequestParam String nomeTeam
    ){
        handler.proclamaVincitore(nomeUtente, nomeHackathon, nomeTeam);
        return ResponseEntity.ok().build();
    }

    /**
     * Metodo della boundary per liquidare il premio al team vincitore
     * @param nomeUtente l'organizzatore che liquida il premio
     * @param nomeHackathon il nome dell'hackathon
     * @param nomeTeam il nome del team
     * @return una nuova chiamata http
     */
    @PostMapping("/{nomeHackathon}/liquidazione-premio")
    public ResponseEntity<Void> attivaLiquidazionePremio(
            @AuthenticationPrincipal String nomeUtente,
            @PathVariable String nomeHackathon,
            @RequestParam String nomeTeam){
        handler.attivaLiquidazionePremio(nomeUtente, nomeHackathon, nomeTeam);
        return ResponseEntity.ok().build();
    }
}
