package unicam.cs.hackhub.servizi.esterni;

import unicam.cs.hackhub.domain.implementazione.CallSlot;
import org.springframework.stereotype.Service;

@Service
public class CalendarioMock implements Calendario {

    /**
     * Questo metodo simula il salvataggio di una call nel calendario esterno, ma in realtà non fa
     * assolutamente niente, ed è solo orientativo per i casi d'uso
     * @param callSlot la durata della call
     */
    @Override
    public void salvaCall(CallSlot callSlot) {}
}
