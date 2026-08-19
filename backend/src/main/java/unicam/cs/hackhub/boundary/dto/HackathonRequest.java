package unicam.cs.hackhub.boundary.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Rappresenta il body JSON per la richiesta di creazione di un hackathon, con tutti i campi necessari per creare un
 * hackathon.
 * @param nome il nome dell'hackathon da creare, che deve essere una stringa non vuota
 * @param dataInizio la data di inizio dell'hackathon, che deve essere una data valida e non nulla
 * @param dataFine la data di fine dell'hackathon, che deve essere una data valida e non nulla
 * @param luogo il luogo in cui si svolgerà l'hackathon, che deve essere una stringa non vuota
 * @param premio il premio in denaro per il vincitore dell'hackathon, che deve essere un numero positivo
 * @param teamMin il numero minimo di persone che devono formare un team per partecipare all'hackathon, che deve essere
 *                un intero compreso tra 3 e 6
 * @param teamMax il numero massimo di persone che possono formare un team per partecipare all'hackathon, che deve
 *                essere un intero compreso tra 3 e 6
 * @param maxIscrizioni il numero massimo di team che possono iscriversi all'hackathon, che deve essere un intero positivo
 * @param regolamento il regolamento dell'hackathon, che deve essere una stringa non vuota
 * @param scadenzaIscrizioni la data e ora di scadenza per le iscrizioni all'hackathon, che deve essere una data valida
 *                           e non nulla
 * @param nomeGiudice il nome dell'utente da invitare come giudice dell'hackathon, che deve essere una stringa non vuota
 * @param nomeMentori la lista dei nomi degli utenti da invitare come mentori dell'hackathon, che deve essere una lista
 *                    non vuota di stringhe non vuote
 */
public record HackathonRequest(
        @NotBlank String nome,
        @NotNull LocalDate dataInizio,
        @NotNull LocalDate dataFine,
        @NotBlank String luogo,
        @NotNull @Positive BigDecimal premio,
        @Min(3) @Max(6) int teamMin,
        @Max(6) @Min(3) int teamMax,
        @Min(1) int maxIscrizioni,
        @NotBlank String regolamento,
        @NotNull LocalDateTime scadenzaIscrizioni,
        @NotBlank String nomeGiudice,
        @NotEmpty @NotNull @Size(min = 1) List<@NotBlank String> nomeMentori
) {
}
