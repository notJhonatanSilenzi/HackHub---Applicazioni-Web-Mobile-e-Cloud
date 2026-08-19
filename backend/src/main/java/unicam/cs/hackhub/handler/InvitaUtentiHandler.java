package unicam.cs.hackhub.handler;

import unicam.cs.hackhub.domain.RuoloTeam;
import unicam.cs.hackhub.domain.implementazione.MembroTeam;
import unicam.cs.hackhub.domain.implementazione.Team;
import unicam.cs.hackhub.domain.implementazione.Utente;
import unicam.cs.hackhub.eccezioni.ConflictException;
import unicam.cs.hackhub.eccezioni.NotFoundException;
import unicam.cs.hackhub.repository.RepositoryMembriTeam;
import unicam.cs.hackhub.repository.RepositoryUtente;
import unicam.cs.hackhub.servizi.ServizioNotifiche;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvitaUtentiHandler {

    private final RepositoryUtente repositoryUtente;
    private final RepositoryMembriTeam repositoryMembriTeam;
    private final ServizioNotifiche servizioNotifiche;

    /**
     * Crea un istanza dell'handler per invitare gli utenti
     *
     * @param repositoryUtente     la repository degli utenti
     * @param repositoryMembriTeam la repository dei membri del team
     * @param servizioNotifiche    il servizio per le notifiche
     */
    public InvitaUtentiHandler(RepositoryUtente repositoryUtente, RepositoryMembriTeam repositoryMembriTeam, ServizioNotifiche servizioNotifiche) {
        this.repositoryUtente = repositoryUtente;
        this.repositoryMembriTeam = repositoryMembriTeam;
        this.servizioNotifiche = servizioNotifiche;
    }

    /**
     * Metodo per invitare utenti ad un team
     *
     * @param nomeUtente           il nome dell'utente da invitare
     * @param nomeUtenteDaInvitare l'utente da invitare
     */
    @Transactional
    public void invitaUtenti(String nomeUtente, String nomeUtenteDaInvitare) {
        MembroTeam leader = repositoryMembriTeam.findByUtente_NomeUtente(nomeUtente).orElseThrow(() -> new NotFoundException("Membro non presente nel team"));
        if (leader.getRuolo() != RuoloTeam.LEADER) {
            throw new ConflictException("Solo il leader può invitare utenti");
        }
        Utente utente = validazioneUtente(nomeUtenteDaInvitare);
        Team team = leader.getTeam();
        servizioNotifiche.creaInvitoTeam(leader.getUtente().getNomeUtente(), utente, team);
    }

    /**
     * Controlla che l'utente da invitare sia valido e non appartenga a nessun team
     * @param nomeUtenteDaInvitare il nome utente
     * @return l'utente
     */
    private Utente validazioneUtente(String nomeUtenteDaInvitare) {
        Utente utente = repositoryUtente.findByNomeUtente(nomeUtenteDaInvitare)
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));
        if (repositoryMembriTeam.findByUtente_NomeUtente(utente.getNomeUtente()).isPresent()) {
            throw new ConflictException("L'utente appartiene già a un team");
        }
        return utente;
    }
}
