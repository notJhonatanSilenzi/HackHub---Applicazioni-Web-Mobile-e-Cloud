package unicam.cs.hackhub.domain.implementazione;

import unicam.cs.hackhub.eccezioni.ConflictException;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Classe che gestisce le tempistiche di inizio e fine di un'hackathon
 */
@Embeddable
public class Periodo {

    @NotNull
    private LocalDate dataInizio;
    @NotNull
    private LocalTime oraInizio;
    @NotNull
    private LocalDate dataFine;
    @NotNull
    private LocalTime oraFine;

    public Periodo() {}

    /**
     * Creazione di un periodo di tempo con validazione per assicurarsi che la data e ora di inizio siano precedenti a
     * quelle di fine
     *
     * @param dataInizio la data d'inizio dell'intervallo
     * @param oraInizio  l'ora d'inizio dell'intervallo dell'intervallo
     * @param dataFine   la data di fine dell'intervallo
     * @param oraFine    l'ora in cui finisce l'intervallo
     */
    public Periodo(LocalDate dataInizio, LocalTime oraInizio, LocalDate dataFine, LocalTime oraFine) {
        validazione(dataInizio, dataFine, oraInizio, oraFine);
        this.dataInizio = dataInizio;
        this.oraInizio = oraInizio;
        this.dataFine = dataFine;
        this.oraFine = oraFine;
    }

    /**
     * Creazione di un periodo di tempo con orari predefiniti (00:00 - 23:59) con validazione per assicurarsi che la
     * data di inizio sia precedente a quella di fine
     *
     * @param dataInizio la data d'inizio dell'intervallo
     * @param dataFine   la data di fine dell'intervallo
     */
    public Periodo(LocalDate dataInizio, LocalDate dataFine) {
        validazione(dataInizio, dataFine, LocalTime.of(0, 0), LocalTime.of(23, 59));
        this.dataInizio = dataInizio;
        this.oraInizio = LocalTime.of(0, 0);
        this.oraFine = LocalTime.of(23, 59);
        this.dataFine = dataFine;
    }

    /**
     * Controlla che i dati inseriti siano validi
     *
     * @param dataInizio la data di inizio
     * @param dataFine   la data di fine
     * @param oraInizio  l'ora di inizio
     * @param oraFine    l'ora di fine
     */
    private void validazione(LocalDate dataInizio, LocalDate dataFine, LocalTime oraInizio, LocalTime oraFine) {
        if (dataInizio.isAfter(dataFine) || (dataInizio.isEqual(dataFine) && oraInizio.isAfter(oraFine))) {
            throw new ConflictException("La data e ora di inizio devono essere precedenti a quelle di fine");
        }
        if (dataInizio.isBefore(LocalDate.now())) {
            throw new ConflictException("La data di inizio deve essere futura a oggi");
        }
    }

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public LocalTime getOraInizio() {
        return oraInizio;
    }

    public LocalDate getDataFine() {
        return dataFine;
    }

    public LocalTime getOraFine() {
        return oraFine;
    }
}
