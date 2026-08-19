package unicam.cs.hackhub.eccezioni;

/**
 * Eccezione lanciata quando si tenta di effettuare una transizione di stato non consentita.
 */
public class TransizioneNonConsentitaException extends RuntimeException {
    public TransizioneNonConsentitaException(String message) {
        super(message);
    }
}
