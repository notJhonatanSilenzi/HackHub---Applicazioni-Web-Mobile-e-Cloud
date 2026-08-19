package unicam.cs.hackhub.domain;

/**
 * Tipi di notifiche inviati ai team e ai rispettivi membri
 */
public enum TipoNotifica {

    /**
     * I team vengono notificati che la valutazione dell'hackathon o della sottomissione è stata conclusa
     */
    VALUTAZIONE_CONCLUSA,

    /**
     * Il mittente viene notificato che il destinatario ha rifiutato la richiesta inviata
     */
    RIFIUTO_RICHIESTA,

    /**
     * Il mittente viene notificato che il destinatario ha accettato la richiesta inviata
     */
    ACCETTA_RICHIESTA,

    /**
     * I membri del team vengono notificati, in quanto un membro del team ha inserito oppure modificato
     * la sottomissione consegnata in precedenza durante l'esecuzione di un hackathon
     */
    SOTTOMISSIONE_MODIFICATA,
    /**
     * I membri del team vengono notificati che la sottomissione in precedenza consegnata nell'hackathon
     * è stata rimossa da un membro del team
     */
    SOTTOMISSIONE_RIMOSSA,
    /**
     * Il leader del team invia a un mentore una richiesta di supporto, in formato notifica
     */
    RICHIESTA_SUPPORTO,

    /**
     * La data di inizio di un hackathon coincide con la data corrente
     */
    AVVIO_HACKATHON,

    /**
     * Un hackathon non soddisfa i requisiti per essere avviato
     */
    IMPOSSIBILE_AVVIARE_HACKATHON,

    /**
     * Un membro del team esce dal suo team
     */
    USCITA,

    /**
     * Il mentore notifica l'organizzatore che un team ha violato il regolamento
     */
    VIOLAZIONE_REGOLAMENTO,

    /**
     * Il mentore espelle un team per violazione del regolamento
     */
    ESPULSIONE_TEAM,

    /**
     * Il leader di un team chiede assistenza ad un mentore legato all'hackathon a cui è iscritto
     */
    ASSISTENZA,

    /**
     * Il leader cambia il nome del team
     */
    CAMBIO_NOME_TEAM,

    /**
     * Trasferisce il ruolo di leader ad un altro membro del team
     */
    TRASFERIMENTO_LEADER,

    /**
     * Il team ha vinto l'hackathon, e i membri del team vengono notificati della vittoria
     */
    VITTORIA,

    /**
     * Il team ha perso l'hackathon e i membri del team vengono notificati della sconfitta
     */
    SCONFITTA,

    HACKATHON_CANCELLATO,

    SCIOGLIMENTO_TEAM
}
