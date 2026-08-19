package unicam.cs.hackhub.eccezioni;

/**
 * Eccezione personalizzata per indicare che l'utente non ha i permessi necessari per accedere a una risorsa o
 * eseguire un'azione.
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
