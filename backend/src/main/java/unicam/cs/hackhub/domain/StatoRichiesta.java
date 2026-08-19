package unicam.cs.hackhub.domain;

/**
 * Gestione degli stati di una richiesta inviata da un team ad un mentore o riguardo inviti allo Staff e hackathon
 */
public enum StatoRichiesta {

    /**
     * La richiesta è stata inviata ma non ha ancora un'esito
     */
    INVIATO,

    /**
     * La richiesta è stata accettata
     */
    ACCETTATO,

    /**
     * La richiesta è stata rifiutata
     */
    RIFIUTATO
}
