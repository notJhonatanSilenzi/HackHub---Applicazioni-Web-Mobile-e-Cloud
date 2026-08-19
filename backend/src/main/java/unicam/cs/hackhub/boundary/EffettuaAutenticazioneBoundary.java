package unicam.cs.hackhub.boundary;

import unicam.cs.hackhub.boundary.dto.AuthResponse;
import unicam.cs.hackhub.boundary.dto.LoginRequest;
import unicam.cs.hackhub.boundary.dto.RegisterRequest;
import unicam.cs.hackhub.handler.EffettuaAutenticazioneHandler;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/autenticazione")
@Validated
public class EffettuaAutenticazioneBoundary {

    private final EffettuaAutenticazioneHandler handler;

    /**
     * Costruttore che inizializza la boundary
     * @param handler l'handler associato a questo boundary
     */
    public EffettuaAutenticazioneBoundary(EffettuaAutenticazioneHandler handler) {
        this.handler = handler;
    }

    /**
     * Metodo che attiva la procedura di registrazione alla piattaforma
     * @param request il JSON di richiesta di registrazione
     * @return la risposta
     */
    @PostMapping("/registrazione")
    public ResponseEntity<Void> attivaRegistrazione(
            @Valid @RequestBody RegisterRequest request) {
        handler.attivaRegistrazione(request); // Avvio la registrazione
        return ResponseEntity.status(HttpStatus.CREATED).build(); // Ritorno il codice di stato created
    }

    /**
     * Metodo che attiva la procedura di login alla piattaforma
     * @param request il JSON di richiesta di login
     * @return la risposta
     */
    @PostMapping("/accesso")
    public ResponseEntity<AuthResponse> attivaAutenticazione(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(handler.attivaAutenticazione(request));
    }
}
