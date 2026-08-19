package unicam.cs.hackhub.handler;

import unicam.cs.hackhub.domain.RuoloTeam;
import unicam.cs.hackhub.domain.implementazione.*;
import unicam.cs.hackhub.domain.implementazione.MembroTeam;
import unicam.cs.hackhub.domain.implementazione.Team;
import unicam.cs.hackhub.domain.implementazione.Utente;
import unicam.cs.hackhub.eccezioni.ConflictException;
import unicam.cs.hackhub.eccezioni.ForbiddenException;
import unicam.cs.hackhub.eccezioni.NotFoundException;
import unicam.cs.hackhub.repository.RepositoryMembriTeam;
import unicam.cs.hackhub.repository.RepositoryTeam;
import unicam.cs.hackhub.repository.RepositoryUtente;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreaTeamHandler {

    private final RepositoryTeam repositoryTeam;
    private final RepositoryMembriTeam repositoryMembriTeam;
    private final RepositoryUtente repositoryUtente;

    /**
     * Costruisce un'entità di CreaTeamHandler che gestisce la creazione di team
     *
     * @param repositoryTeam       la repository per controllare se il nome del team già esiste
     * @param repositoryMembriTeam la repository per controllare se l'utente è già membro di un team
     * @param repositoryUtente     la repository per recuperare l'utente che vuole creare il team
     */
    public CreaTeamHandler(RepositoryTeam repositoryTeam, RepositoryMembriTeam repositoryMembriTeam, RepositoryUtente
            repositoryUtente) {
        this.repositoryTeam = repositoryTeam;
        this.repositoryMembriTeam = repositoryMembriTeam;
        this.repositoryUtente = repositoryUtente;
    }

    /**
     * Avvia la creazione di un team, verificando che l'utente non sia già membro di un team e che il nome del team
     * non sia già esistente. Se tutte le verifiche passano, crea un nuovo team e aggiunge l'utente come membro con
     * ruolo di leader.
     *
     * @param nomeUtente il nome utente dell'utente che vuole creare il team
     * @param nomeTeam   il nome del team da creare
     */
    @Transactional
    public void avviaCreazioneTeam(String nomeUtente, String nomeTeam) {
        Utente utente = repositoryUtente.findByNomeUtente(nomeUtente).orElseThrow(() ->
                new NotFoundException("Utente non trovato"));
        if (repositoryMembriTeam.existsByUtente(utente)) {
            throw new ForbiddenException("L'utente è già membro di un team");
        }
        if (repositoryTeam.existsByNome(nomeTeam)) {
            throw new ConflictException("Esiste già un team con questo nome");
        }
        Team team = new Team(nomeTeam);
        MembroTeam leader = new MembroTeam(utente, team, RuoloTeam.LEADER);
        team.setLeader(leader);
        repositoryTeam.save(team);
    }
}
