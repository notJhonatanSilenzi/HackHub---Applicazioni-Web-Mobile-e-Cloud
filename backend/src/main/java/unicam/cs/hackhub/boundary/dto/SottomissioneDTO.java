package unicam.cs.hackhub.boundary.dto;

import jakarta.validation.constraints.NotBlank;

public record SottomissioneDTO(
        @NotBlank String link,
        String giudizio,
        int punteggio
) {
}
