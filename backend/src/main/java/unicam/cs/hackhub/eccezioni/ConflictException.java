package unicam.cs.hackhub.eccezioni;

/**
 * Eccezione lanciata quando si verifica un conflitto, ad esempio quando si tenta di creare una risorsa che già esiste
 * o quando si verifica una violazione di integrità dei dati.
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
