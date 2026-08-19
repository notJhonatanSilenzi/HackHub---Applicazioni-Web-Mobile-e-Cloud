package unicam.cs.hackhub.eccezioni;

/**
 * Eccezione personalizzata per gestire errori di richiesta non valida (HTTP 400 Bad Request).
 * Questa eccezione viene sollevata quando il client invia una richiesta che non può essere elaborata
 * a causa di dati errati, mancanti o formattati in modo non corretto.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
