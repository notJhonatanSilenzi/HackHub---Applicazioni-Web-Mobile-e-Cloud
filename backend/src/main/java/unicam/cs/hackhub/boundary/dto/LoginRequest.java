package unicam.cs.hackhub.boundary.dto;

import jakarta.validation.constraints.*;

/**
 * dto che rappresenta il body JSON per la richiesta di login alla piattaforma
 * @param nomeUtente il nomeUtente inserito dal Visitatore che tenta di accedere
 * @param password la password salvata nel db
 */
public record LoginRequest(
        @NotBlank String nomeUtente,
        @NotBlank @Size(min = 6) String password
) {
}
