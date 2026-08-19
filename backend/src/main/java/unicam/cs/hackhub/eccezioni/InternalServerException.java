package unicam.cs.hackhub.eccezioni;

/**
 * Eccezione personalizzata per indicare un errore interno del server.
 * Questa eccezione viene sollevata quando si verifica un problema imprevisto durante l'elaborazione di una richiesta.
 */
public class InternalServerException extends RuntimeException {
    public InternalServerException(String message) {
        super(message);
    }
}
