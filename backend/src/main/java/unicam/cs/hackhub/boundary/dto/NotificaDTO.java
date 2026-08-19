package unicam.cs.hackhub.boundary.dto;

import jakarta.validation.constraints.NotBlank;

public record NotificaDTO(
        @NotBlank String messaggio
) {
}
