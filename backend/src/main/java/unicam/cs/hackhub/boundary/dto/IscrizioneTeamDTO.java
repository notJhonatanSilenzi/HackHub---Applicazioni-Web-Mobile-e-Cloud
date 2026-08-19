package unicam.cs.hackhub.boundary.dto;

import jakarta.validation.constraints.NotBlank;

public record IscrizioneTeamDTO(
        @NotBlank String nomeHackathon,
        @NotBlank String nomeTeam,
        String linkSottomissione
        ) {
}
