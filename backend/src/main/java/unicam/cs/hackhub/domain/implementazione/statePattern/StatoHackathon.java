package unicam.cs.hackhub.domain.implementazione.statePattern;

import unicam.cs.hackhub.domain.implementazione.Hackathon;
import unicam.cs.hackhub.eccezioni.TransizioneNonConsentitaException;

/**
 * interfaccia che gestisce lo stato dell'hackathon avviando o concludendo iscrizioni ed eventi
 */
public interface StatoHackathon {

    /**
     * Chiude le iscrizioni per un certo Hackathon
     *
     * @param hackathon l'evento considerato
     */
    default void chiudiIscrizioni(Hackathon hackathon) {
        throw new TransizioneNonConsentitaException("Non è possibile chiudere le iscrizioni nella fase attuale");
    }

    /**
     * Avvio dell'hackathon
     *
     * @param hackathon l'evento considerato
     */
    default void avviaHackathon(Hackathon hackathon) {
        throw new TransizioneNonConsentitaException("Non è possibile avviare l'hackathon nella fase attuale");
    }

    /**
     * Conclusione dell'hackathon
     *
     * @param hackathon l'evento considerato
     */
    default void concludiHackathon(Hackathon hackathon) {
        throw new TransizioneNonConsentitaException("Non è possibile concludere l'hackathon nella fase attuale");
    }

    default void avviaValutazione(Hackathon hackathon) {
        throw new TransizioneNonConsentitaException("Non è possibile avviare la valutazione dell'hackathon nella fase attuale");
    }

    default void verificaIscrizioneConsentita(Hackathon h) {
        throw new TransizioneNonConsentitaException("Non è possibile iscriversi in questa fase dell'hackathon");
    }

    default void verificaInvioSottomissioneConsentito(Hackathon h) {
        throw new TransizioneNonConsentitaException("Non è possibile inviare sottomissioni in questa fase dell'hackathon");
    }

    default void verificaValutazioneConsentita(Hackathon h) {
        throw new TransizioneNonConsentitaException("Non è possibile valutare le sottomissioni in questa fase dell'hackathon");
    }

    default void verificaNominaMentoriConsentita(Hackathon h) {
        throw new TransizioneNonConsentitaException("Non è possibile nominare mentori in questa fase dell'hackathon");
    }

    default void verificaEliminazioneConsentita(Hackathon h) {
        throw new TransizioneNonConsentitaException("Non è possibile eliminare l'hackathon in questa fase");
    }

    default void verificaEspulsioneTeamConsentita(Hackathon h) {
        throw new TransizioneNonConsentitaException("Non è possibile espellere team in questa fase dell'hackathon");
    }

    default void verificaProclamazioneConsentita(Hackathon h) {
        throw new TransizioneNonConsentitaException("Non è possibile proclamare il vincitore in questa fase dell'hackathon");
    }

    default void verificaLiquidazionePremioConsentita(Hackathon h) {
        throw new TransizioneNonConsentitaException("Non è possibile liquidare il premio in questa fase dell'hackathon");
    }

    default void verificaAnnullamentoIscrizioneConsentito(Hackathon h) {
        throw new TransizioneNonConsentitaException("Non è possibile annullare l'iscrizione in questa fase dell'hackathon");
    }

    default void verificaPropostaDiCallConsentita(Hackathon h) {
        throw new TransizioneNonConsentitaException("Non è possibile proporre una call in questa fase dell'hackathon");
    }
}
