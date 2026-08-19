package unicam.cs.hackhub.eccezioni;

/**
 * Eccezione personalizzata per indicare che una risorsa richiesta non è stata trovata.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
