package unicam.cs.hackhub.repository;

import unicam.cs.hackhub.domain.implementazione.Notifica;
import unicam.cs.hackhub.domain.implementazione.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepositoryNotifica extends JpaRepository<Notifica, String> {

    /**
     * Trova le notifiche con un determinato destinatario
     *
     * @param destinatario il destinatario
     * @return la lista delle notifiche con quel destinatario, se non ci sono ritorna una lista vuota
     */
    List<Notifica> findAllByDestinatario(Utente destinatario);

    /**
     * Trova una notifica dal suo id
     *
     * @param idNotifica l'id della notifica
     * @return la notifica se esiste, altrimenti un optional vuoto
     */
    Optional<Notifica> findByIdNotifica(String idNotifica);
}
