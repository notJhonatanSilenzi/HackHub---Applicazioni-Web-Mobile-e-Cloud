package unicam.cs.hackhub.repository;

import unicam.cs.hackhub.domain.implementazione.Valutazione;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositoryValutazioni extends JpaRepository<Valutazione, String> {
}
