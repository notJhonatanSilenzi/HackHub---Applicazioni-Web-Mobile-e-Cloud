package unicam.cs.hackhub.domain;

/**
 * Questo enum serve a simulare il pattern State per la persistenza nel db
 */
public enum StatoEnum {
    CONCLUSO,
    IN_CORSO,
    ISCRIZIONI_APERTE,
    ISCRIZIONI_CHIUSE,
    VALUTAZIONE_IN_CORSO
}
