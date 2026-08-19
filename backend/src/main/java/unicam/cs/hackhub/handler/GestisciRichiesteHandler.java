package unicam.cs.hackhub.handler;

import unicam.cs.hackhub.domain.RuoloStaff;
import unicam.cs.hackhub.domain.RuoloTeam;
import unicam.cs.hackhub.domain.StatoRichiesta;
import unicam.cs.hackhub.domain.TipoNotifica;
import unicam.cs.hackhub.domain.implementazione.*;
import unicam.cs.hackhub.domain.implementazione.*;
import unicam.cs.hackhub.eccezioni.ConflictException;
import unicam.cs.hackhub.eccezioni.ForbiddenException;
import unicam.cs.hackhub.eccezioni.NotFoundException;
import unicam.cs.hackhub.repository.*;
import unicam.cs.hackhub.repository.*;
import unicam.cs.hackhub.servizi.ServizioNotifiche;
import unicam.cs.hackhub.servizi.esterni.CalendarioMock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class GestisciRichiesteHandler {

    private final RepositoryUtente repositoryUtente;
    private final RepositoryRichiesta repositoryRichiesta;
    private final RepositoryHackathon repositoryHackathon;
    private final ServizioNotifiche servizioNotifiche;
    private final RepositoryStaff repositoryStaff;
    private final RepositoryMembriTeam repositoryMembriTeam;
    private final CalendarioMock calendario;

    /**
     * Istanzia un handler che si occupa di gestire tutti gli inviti, sia per lo Staff sia per i Team
     *
     * @param repositoryUtente    la repository per gli utente
     * @param repositoryRichiesta la repository per le richieste
     * @param repositoryHackathon la repository per gli hackathon
     * @param servizioNotifiche   il servizio che gestisce le notifiche
     */
    public GestisciRichiesteHandler(RepositoryUtente repositoryUtente, RepositoryRichiesta repositoryRichiesta, RepositoryHackathon repositoryHackathon, ServizioNotifiche servizioNotifiche, RepositoryStaff repositoryStaff, RepositoryMembriTeam repositoryMembriTeam, CalendarioMock calendario) {
        this.repositoryUtente = repositoryUtente;
        this.repositoryRichiesta = repositoryRichiesta;
        this.repositoryHackathon = repositoryHackathon;
        this.servizioNotifiche = servizioNotifiche;
        this.repositoryStaff = repositoryStaff;
        this.repositoryMembriTeam = repositoryMembriTeam;
        this.calendario = calendario;
    }

    /**
     * Metodo del boundary che accetta una richiesta di invito Staff
     *
     * @param nomeUtente  il nome utente dell'utente che accetta la richiesta
     * @param idRichiesta l'identificativo della richeista
     */
    @Transactional
    public void accettaRichiesta(String nomeUtente, String idRichiesta) {
        Utente utente = validazioneUtente(nomeUtente);
        Richiesta r = validazioneRichiesta(idRichiesta);
        validaDestinatarioRichiesta(utente, r);
        validaRichiestaElaborabile(r);
        Utente destinatario;

        switch (r) {
            case InvitoStaff invitoStaff -> {
                r.accetta();
                destinatario = accettaInvitoStaff(nomeUtente, invitoStaff);
            }
            case InvitoTeam invitoTeam -> {
                destinatario = accettaInvitoTeam(nomeUtente, invitoTeam);
                r.accetta();
            }
            case PropostaCall propostaCall -> {
                r.accetta();
                destinatario = accettaCall(nomeUtente, propostaCall);
            }
            default -> throw new ConflictException("La richiesta non appartiene a nessun tipo di invito esistente");
        }
        servizioNotifiche.creaNotifica(destinatario, TipoNotifica.ACCETTA_RICHIESTA, utente.getNomeUtente() + " ha accettato la tua richiesta");
    }

    /**
     * Metodo che gestisce il rifiuto ad una richiesta
     *
     * @param nomeUtente  il nome dell'utente
     * @param idRichiesta l'identificativo della richiesta
     */
    @Transactional
    public void rifiutaRichiesta(String nomeUtente, String idRichiesta) {
        Utente utente = validazioneUtente(nomeUtente);
        Richiesta r = validazioneRichiesta(idRichiesta);
        validaDestinatarioRichiesta(utente, r);
        validaRichiestaElaborabile(r);
        r.rifiuta();
        Utente mittente = trovaMittenteRichiesta(r);
        servizioNotifiche.creaNotifica(mittente, TipoNotifica.RIFIUTO_RICHIESTA, utente.getNomeUtente() + " ha rifiutato la tua richiesta");
    }

    /**
     * Metodo che gestisce l'accettazione di un invito per lo staff
     *
     * @param nomeUtente  il nome dell'utente
     * @param invitoStaff l'invito
     * @return l'organizzatore dell'hackathon a cui è stato invitato lo staff, così da poterlo notificare dell'accettazione dell'invito
     */
    private Utente accettaInvitoStaff(String nomeUtente, InvitoStaff invitoStaff) {
        validazioneUtente(nomeUtente);
        Hackathon hackathon = invitoStaff.getHackathon();
        repositoryHackathon.save(hackathon);
        return trovaOrganizzatore(hackathon);
    }

    /**
     * Metodo che gestisce l'accettazione di un invito per i team
     *
     * @param nomeUtente il nome dell'utente
     * @param invitoTeam l'invito
     * @return il leader del team da notificare
     */
    private Utente accettaInvitoTeam(String nomeUtente, InvitoTeam invitoTeam) {
        repositoryMembriTeam.findByUtente_NomeUtente(nomeUtente).ifPresent(m -> {
            throw new ForbiddenException("L'utente è già membro di un team");
        });
        validazioneUtente(nomeUtente);
        Team team = invitoTeam.getTeam();
        return trovaLeader(team);
    }

    /**
     * Accetta una call
     *
     * @param nomeUtente   il nome dell'utente che accetta la call
     * @param propostaCall la proposta di call
     * @return il mentore che accetta la call
     */
    public Utente accettaCall(String nomeUtente, PropostaCall propostaCall) {
        validazioneUtente(nomeUtente);
        String link = "";
        Team team = repositoryMembriTeam
                .findByUtente_NomeUtente(propostaCall.getDestinatario().getNomeUtente())
                .orElseThrow(() -> new NotFoundException("L'utente non appartiene a nessun team"))
                .getTeam();
        Staff mentore = repositoryStaff.findByUtente_NomeUtente(propostaCall.getMittente())
                .orElseThrow(() -> new NotFoundException("Mentore non trovato"));
        CallSlot callSlot = new CallSlot(propostaCall.getPeriodo(), team, mentore, link);
        calendario.salvaCall(callSlot);
        return mentore.getUtente();
    }

    /**
     * Trova il leader del team
     *
     * @param team il team
     * @return l'utente se è valido, altrimenti lancia un'eccezione
     */
    private Utente trovaLeader(Team team) {
        return team.getMembri().stream().filter(
                        m -> m.getRuolo() == RuoloTeam.LEADER).
                map(MembroTeam::getUtente).
                findFirst().
                orElseThrow(
                        () -> new NotFoundException("Utente non trovato"));
    }

    /**
     * Controlla che l'utente sia valido
     *
     * @param nomeUtente il nome dell'utente
     * @return l'utente se è valido, altrimenti lancia un'eccezione
     */
    private Utente validazioneUtente(String nomeUtente) {
        return repositoryUtente.findByNomeUtente(nomeUtente)
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));
    }

    /**
     * Controlla che la richiesta sia valida
     *
     * @param idRichiesta l'id della richiesta
     * @return la richiesta se è valida, altrimenti lancia un'eccezione
     */
    private Richiesta validazioneRichiesta(String idRichiesta) {
        return repositoryRichiesta.findById(idRichiesta)
                .orElseThrow(() -> new NotFoundException("Invito scaduto"));
    }

    private void validaDestinatarioRichiesta(Utente utente, Richiesta richiesta) {
        if (!richiesta.getDestinatario().getNomeUtente().equals(utente.getNomeUtente())) {
            throw new ForbiddenException("L'utente autenticato non è il destinatario della richiesta");
        }
    }

    private void validaRichiestaElaborabile(Richiesta richiesta) {
        if (richiesta.getScadenza().isBefore(LocalDateTime.now())) {
            throw new ConflictException("La richiesta è scaduta");
        }
        if (richiesta.getStato() != StatoRichiesta.INVIATO) {
            throw new ConflictException("La richiesta è già stata elaborata");
        }
    }

    private Utente trovaMittenteRichiesta(Richiesta richiesta) {
        return repositoryUtente.findByNomeUtente(richiesta.getMittente())
                .orElseThrow(() -> new NotFoundException("Mittente non trovato"));
    }

    /**
     * Trova l'organizzatore dell'hackathon
     *
     * @param hackathon l'hackathon
     * @return l'organizzatore se è valido, altrimenti lancia un'eccezione
     */
    private Utente trovaOrganizzatore(Hackathon hackathon) {
        return hackathon.getStaff().stream().filter(
                        s -> s.getRuolo() == RuoloStaff.ORGANIZZATORE)
                .map(Staff::getUtente)
                .findFirst()
                .orElseThrow(() ->
                        new NotFoundException("Organizzatore non trovato"));
    }
}
