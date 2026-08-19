package unicam.cs.hackhub.boundary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO per la richiesta di proposta di call per un hackathon.
 * @param nomeHackathon l'hackathon per cui si vuole proporre la call, che deve essere un nome valido e non nullo
 * @param nomeTeam il team a cui viene proposta la call, che deve essere un nome valido e non nullo
 * @param data la data in cui si vuole tenere la call, che deve essere una data valida e non nulla
 * @param ora  l'ora in cui si vuole tenere la call, che deve essere un'ora valida e non nulla
 */
public record PropostaCallRequest(
        @NotBlank String nomeHackathon,
        @NotBlank String nomeTeam,
        @NotNull LocalDate data,
        @NotNull LocalTime ora
) { }
