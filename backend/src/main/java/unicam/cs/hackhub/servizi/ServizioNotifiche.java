package unicam.cs.hackhub.servizi;

import unicam.cs.hackhub.domain.RuoloStaff;
import unicam.cs.hackhub.domain.TipoNotifica;
import unicam.cs.hackhub.domain.implementazione.*;
import unicam.cs.hackhub.domain.implementazione.*;
import unicam.cs.hackhub.eccezioni.ConflictException;
import unicam.cs.hackhub.repository.RepositoryNotifica;
import unicam.cs.hackhub.repository.RepositoryRichiesta;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Pattern: Singleton, gestione delle notifiche
 */
@Service
public class ServizioNotifiche {

    private final RepositoryRichiesta repositoryRichiesta;
    private final RepositoryNotifica repositoryNotifica;

    /**
     * Crea una nuovo servizio notifiche
     *
     * @param repositoryNotifica  il repository delle notifiche
     * @param repositoryRichiesta il repository delle richieste
     */
    public ServizioNotifiche(RepositoryNotifica repositoryNotifica, RepositoryRichiesta repositoryRichiesta) {
        this.repositoryNotifica = repositoryNotifica;
        this.repositoryRichiesta = repositoryRichiesta;
    }

    /**
     * Crea una nuova notifica
     *
     * @param destinatario i destinatari
     * @param tipo         il tipo della notifica
     * @param messaggio    il messaggio da inviare
     */
    public void creaNotifica(Utente destinatario, TipoNotifica tipo, String messaggio) {
        Notifica notifica = new Notifica(messaggio, destinatario, tipo);
        repositoryNotifica.save(notifica);
    }

    /**
     * Crea una proposta di call
     *
     * @param nomeMittente il nome del mittente
     * @param destinatario il nome del destinatario
     * @param periodo      la durata della call
     */
    public void creaPropostaCall(String nomeMittente, Utente destinatario, Periodo periodo) {
        PropostaCall propostaCall = new PropostaCall(nomeMittente,
                "Proposta di Call da " + nomeMittente + ", per il giorno" + periodo.getDataInizio() + ", nell'orario: " + periodo.getDataInizio() + " - " + periodo.getOraFine(),
                destinatario,
                LocalDateTime.of(periodo.getDataInizio().minusDays(1), periodo.getOraInizio()),
                periodo);
        repositoryRichiesta.save(propostaCall);
    }

    /**
     * Metodo che istanzia un Invito allo Staff di un hackathon
     *
     * @param nomeMittente il nome del mittente
     * @param destinatario il destinatario
     * @param hackathon    l'hackathon associato
     * @param ruolo        il ruolo offerto
     */
    public void creaInvitoStaff(String nomeMittente, Utente destinatario, Hackathon hackathon, RuoloStaff ruolo) {
        if (ruolo.equals(RuoloStaff.ORGANIZZATORE))
            throw new ConflictException("Ruolo non assegnabile");

        InvitoStaff invitoStaff = new InvitoStaff(
                nomeMittente,
                "Invito nello Staff di " + hackathon.getNome() + " come " + ruolo.name() + " da " + nomeMittente,
                destinatario,
                LocalDateTime.now(),
                hackathon,
                ruolo);
        repositoryRichiesta.save(invitoStaff);
    }

    /**
     * Crea un invito da inviare per entrare in un team
     *
     * @param nomeMittente il nome del mittente
     * @param destinatario il destinatario
     * @param team         il team associato
     */
    public void creaInvitoTeam(String nomeMittente, Utente destinatario, Team team) {
        InvitoTeam invitoTeam = new InvitoTeam(
                nomeMittente,
                "Invito ad entrare nel team: " + team.getNome() + "da " + nomeMittente,
                destinatario,
                LocalDateTime.now().plusDays(3),
                team
        );
        repositoryRichiesta.save(invitoTeam);
    }

}
