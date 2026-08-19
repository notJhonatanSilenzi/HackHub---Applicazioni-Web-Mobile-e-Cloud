package unicam.cs.hackhub.repository;

import unicam.cs.hackhub.domain.StatoEnum;
import unicam.cs.hackhub.domain.implementazione.Hackathon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface RepositoryHackathon extends JpaRepository<Hackathon, String> {

    /**
     * Controlla che esista un hackathon con il nome specificato
     *
     * @param nome il nome
     * @return true se esiste, false altrimenti
     */
    boolean existsByNome(String nome);

    /**
     * Trova l'hackathon con il nome specificato
     *
     * @param nomeHackathon il nome dell'hackathon
     * @return l'hackathon, un optional vuoto se non esiste
     */
    Optional<Hackathon> findByNome(String nomeHackathon);


    /**
     * Recupera un Hackathon insieme alla sua collezione di staff usando JOIN FETCH per evitare problemi di lazy loading
     * quando l'entità viene consultata fuori dal contesto transazionale (es. nei test).
     *
     * @param idHackathon l'id dell'hackathon
     * @return l'hackathon con lo staff inizializzato
     */
    @Query("SELECT h FROM Hackathon h LEFT JOIN FETCH h.staff WHERE h.idHackathon = :idHackathon")
    Optional<Hackathon> findByIdFetchStaff(@Param("idHackathon") String idHackathon);

    /**
     * Query per trovare tutti gli hackathon che hanno iscrizioni chiuse e la data di inizio passata, quindi pronti per essere avviati
     *
     * @param stato   lo stato dell'hackathon
     * @param today   la data corrente
     * @param nowTime l'ora corrente
     * @return la lista degli hackathon da avviare
     */
    @Query("""
            SELECT h\s
            FROM Hackathon h
            WHERE h.statoEnum = :stato
            AND (h.periodo.dataInizio <= :today
            AND h.periodo.oraInizio <= :nowTime)
            ORDER BY h.periodo.dataInizio ASC, h.periodo.oraInizio ASC
                       \s""")
    List<Hackathon> findHackathonDaAvviare(
            @Param("stato") StatoEnum stato,
            @Param("today") LocalDate today,
            @Param("nowTime") LocalTime nowTime);

    /**
     * Query che trova tutti gli hackathon di cui si devono chiudere le iscrizioni
     *
     * @param statoEnum lo stato dell'hackathon
     * @param scadenza  la data di scadenza
     * @return la lista degli hackathon di cui si devono chiudere le iscrizioni
     */
    @Query("""
            SELECT h
            FROM Hackathon h
            WHERE h.statoEnum = :stato
            AND h.scadenzaIscrizioni <= :scadenza
            ORDER BY h.periodo.dataInizio ASC, h.periodo.oraInizio ASC
                           \s""")
    List<Hackathon> findHackathonDaChiudere(
            @Param("stato") StatoEnum statoEnum,
            @Param("scadenza") LocalDateTime scadenza
    );

    /**
     * Query che trova tutti gli hackathon da valutare
     *
     * @param statoEnum lo stato dell'hackathon
     * @param scadenza  la scadenza della consegna delle sottomissioni
     * @return la lista degli hackathon da valutare
     */
    @Query("""
            SELECT h
            FROM Hackathon h
            WHERE h.statoEnum = :stato
            AND h.periodo.dataFine <= :scadenza
            ORDER BY h.periodo.dataInizio ASC, h.periodo.oraInizio ASC
                           \s""")
    List<Hackathon> findHackathonDaValutare(
            @Param("stato") StatoEnum statoEnum,
            @Param("scadenza") LocalDateTime scadenza
    );
}
