package unicam.cs.hackhub.boundary.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/*
Questo rappresenta il body JSON tipo:
{ "giudizio": "…", "punteggio": 8 }
 */

/**
 * Rappresenta la richiesta per inserire una valutazione su una sottomissione.
 * @param giudizio il giudizio scritto del giudice sulla sottomissione, che deve essere una stringa non vuota
 * @param punteggio il punteggio numerico assegnato alla sottomissione, che deve essere un intero compreso tra 0 e 10
 */
public record ValutazioneRequest(
        @NotBlank String giudizio,
        @Min(0) @Max(10) int punteggio
) {

}
