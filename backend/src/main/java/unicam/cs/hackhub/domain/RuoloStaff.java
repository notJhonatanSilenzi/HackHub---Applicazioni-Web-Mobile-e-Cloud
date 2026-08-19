package unicam.cs.hackhub.domain;

public enum RuoloStaff {

    /**
     * Si occupa di avviare e gestire l'hackathon, ha tutte i privilegi per l'evento da lui creato
     */
    ORGANIZZATORE,

    /**
     * Aiuta i team tramite call fornendo assistenza per l'hackathon a cui è associato
     */
    MENTORE,

    /**
     * Visualizza e valuta le sottomissioni decretando il team vincitore dell'hackathon
     */
    GIUDICE
}
