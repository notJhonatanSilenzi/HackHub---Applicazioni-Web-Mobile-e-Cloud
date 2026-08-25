package unicam.cs.hackhub.boundary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InfoUtenteDTO(
        @NotNull @NotBlank String nomeUtente,
        @NotNull @NotBlank String email,
        @NotNull @NotBlank String nomeTeam
) {
}
