package unicam.cs.hackhub.boundary.dto;

import jakarta.validation.constraints.*;

/**
 * dto che rappresenta il body JSON per la richiesta di registrazione alla piattaforma da parte di un Visitatore
 * @param nomeUtente il nomeUtente scelto dal Visitatore
 * @param email la mail del Visitatore
 * @param password la password scelta
 */
public record RegisterRequest(
        @NotBlank String nomeUtente,
        @Email @NotBlank String email,
        @NotBlank @Size(min = 6) String password
) {
}
