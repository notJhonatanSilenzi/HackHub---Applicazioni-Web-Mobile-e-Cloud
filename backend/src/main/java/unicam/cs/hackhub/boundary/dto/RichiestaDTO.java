package unicam.cs.hackhub.boundary.dto;

import jakarta.validation.constraints.*;

/**
 * DTO che rappresenta un invito a entrare a far parte dello Staff di un hackathon o di un Team
 */
public record RichiestaDTO(
    @NotBlank String idRichiesta,
    @NotBlank String payload
) {
}
