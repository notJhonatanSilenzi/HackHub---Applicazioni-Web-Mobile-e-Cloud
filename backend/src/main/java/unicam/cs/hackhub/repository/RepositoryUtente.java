package unicam.cs.hackhub.repository;

import unicam.cs.hackhub.domain.implementazione.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepositoryUtente extends JpaRepository<Utente, String> {

    /**
     * Trova un utente dal suo nome
     *
     * @param nomeUtente il nome dell'utente
     * @return l'utente se presente
     */
    Optional<Utente> findByNomeUtente(String nomeUtente);

}
