package unicam.cs.hackhub.handler;

import unicam.cs.hackhub.eccezioni.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import unicam.cs.hackhub.eccezioni.*;

/**
 * Handler globale per le eccezioni nelle API REST. Utilizza @RestControllerAdvice per intercettare le eccezioni
 * e restituire risposte JSON coerenti con i codici di stato HTTP appropriati
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * Classe record per rappresentare un errore API con un messaggio. Utilizzata per restituire risposte coerenti
     *
     * @param message Il messaggio di errore da restituire al client
     */
    public record ApiError(String message) {
    }

    /**
     * Gestisce le eccezioni di tipo NotFoundException, restituendo una risposta con codice 404 e un messaggio di errore
     *
     * @param ex L'eccezione NotFoundException catturata
     * @return Una ResponseEntity contenente un oggetto ApiError con il messaggio dell'eccezione e il codice HTTP 404
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(ex.getMessage()));
    }

    /**
     * Gestisce le eccezioni di tipo ForbiddenException, restituendo una risposta con codice 403 e un messaggio di
     * errore
     *
     * @param ex L'eccezione ForbiddenException catturata
     * @return Una ResponseEntity contenente un oggetto ApiError con il messaggio dell'eccezione e il codice HTTP 403
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiError> handleForbidden(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiError(ex.getMessage()));
    }

    /**
     * Gestisce le eccezioni di tipo ConflictException, restituendo una risposta con codice 409 e un messaggio di errore
     *
     * @param ex L'eccezione ConflictException catturata
     * @return Una ResponseEntity contenente un oggetto ApiError con il messaggio dell'eccezione e il codice HTTP 409
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(ex.getMessage()));
    }

    /**
     * Gestisce le eccezioni di tipo BadRequestException, restituendo una risposta con codice 400 e un messaggio di
     * errore
     *
     * @param ex L'eccezione BadRequestException catturata
     * @return Una ResponseEntity contenente un oggetto ApiError con il messaggio dell'eccezione e il codice HTTP 400
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(ex.getMessage()));
    }

    /**
     * Gestisce tutte le eccezioni generiche non previste, restituendo una risposta con codice 500 e un messaggio di
     * errore generico
     *
     * @param ex L'eccezione generica catturata
     * @return Una ResponseEntity contenente un oggetto ApiError con un messaggio generico di errore interno e il codice
     * HTTP 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError(ex.getMessage()));
    }

    /**
     * Gestisce le eccezioni di validazione dei parametri, restituendo una risposta con codice 400 e un messaggio di
     * errore dettagliato basato sul primo errore di validazione riscontrato
     *
     * @param ex L'eccezione MethodArgumentNotValidException catturata, che contiene i dettagli degli errori di
     *           validazione
     * @return Una ResponseEntity contenente un oggetto ApiError con un messaggio dettagliato del primo errore di
     * validazione e il codice HTTP 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .orElse("Richiesta non valida");
        return ResponseEntity.badRequest().body(new ApiError(msg));
    }

    /**
     * Gestisce le eccezioni di tipo InternalServerException, restituendo una risposta con codice 500 e un messaggio di
     * errore basato sul messaggio dell'eccezione
     *
     * @param ex L'eccezione InternalServerException catturata, che contiene un messaggio di errore dettagliato
     * @return Una ResponseEntity contenente un oggetto ApiError con il messaggio dell'eccezione e il codice HTTP 500
     */
    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ApiError> handleInternalServer(InternalServerException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError(ex.getMessage()));
    }

    /**
     * Gestisce le eccezioni di tipo TransizioneNonConsentitaException, restituendo una risposta con codice 409 e un
     * messaggio di errore basato sul messaggio dell'eccezione
     *
     * @param ex L'eccezione TransizioneNonConsentitaException catturata, che contiene un messaggio di errore dettagliato
     * @return Una ResponseEntity contenente un oggetto ApiError con il messaggio dell'eccezione e il codice HTTP 409
     */
    @ExceptionHandler(TransizioneNonConsentitaException.class)
    public ResponseEntity<ApiError> handleTransizioneNonConsentita(TransizioneNonConsentitaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(ex.getMessage()));
    }

    /**
     * Gestisce le eccezioni di tipo IllegalArgumentException, restituendo una risposta con codice 400 e un messaggio di
     * errore basato sul messaggio dell'eccezione.
     *
     * @param ex L'eccezione IllegalArgumentException catturata, che contiene un messaggio di errore dettagliato
     * @return Una ResponseEntity contenente un oggetto ApiError con il messaggio dell'eccezione e il codice HTTP 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(ex.getMessage()));
    }


}
