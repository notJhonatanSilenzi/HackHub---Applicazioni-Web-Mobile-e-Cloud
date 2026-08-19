package unicam.cs.hackhub.handler;

import unicam.cs.hackhub.domain.TipoNotifica;
import unicam.cs.hackhub.domain.implementazione.*;
import unicam.cs.hackhub.domain.implementazione.*;
import unicam.cs.hackhub.eccezioni.ConflictException;
import unicam.cs.hackhub.eccezioni.NotFoundException;
import unicam.cs.hackhub.repository.RepositoryHackathon;
import unicam.cs.hackhub.repository.RepositoryIscrizioniTeam;
import unicam.cs.hackhub.repository.RepositoryMembriTeam;
import unicam.cs.hackhub.servizi.ServizioNotifiche;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GestisceSottomissioneHandler {

    private final RepositoryIscrizioniTeam repositoryIscrizioniTeam;
    private final RepositoryMembriTeam repositoryMembriTeam;
    private final ServizioNotifiche servizioNotifiche;
    private final RepositoryHackathon repositoryHackathon;

    /**
     * Metodo che istanzia l'handler per la gestione delle sottomissioni
     *
     * @param repositoryIscrizioniTeam la repo per le iscrizioni agli hackathon
     * @param repositoryMembriTeam     la repo per i membri team
     * @param servizioNotifiche        il servizio per l'invio delle notifiche
     */
    public GestisceSottomissioneHandler(RepositoryIscrizioniTeam repositoryIscrizioniTeam, RepositoryMembriTeam repositoryMembriTeam, ServizioNotifiche servizioNotifiche, RepositoryHackathon repositoryHackathon) {
        this.repositoryIscrizioniTeam = repositoryIscrizioniTeam;
        this.repositoryMembriTeam = repositoryMembriTeam;
        this.servizioNotifiche = servizioNotifiche;
        this.repositoryHackathon = repositoryHackathon;
    }

    /**
     * Metodo che crea una sottomissione e la invia
     *
     * @param nomeUtente    il nome utente del membro che invia la sottomissione
     * @param link          il link a un file online o a una repository di GitHub
     * @param nomeHackathon il nome dell'hackathon
     */
    @Transactional
    public void inviaSottomissione(String nomeUtente, String nomeHackathon, String link) {
        MembroTeam membro = validaAutorizzazione(nomeUtente);
        Team team = membro.getTeam();
        IscrizioneTeam iscrizioneTeam = repositoryIscrizioniTeam.findByTeam(team)
                .orElseThrow(() -> new NotFoundException("Team non trovato"));
        Hackathon hackathon = checkStessoHackathon(nomeHackathon, iscrizioneTeam);
        hackathon.getStato().verificaInvioSottomissioneConsentito(iscrizioneTeam.getHackathon());
        Sottomissione sottomissione = new Sottomissione(link);
        iscrizioneTeam.aggiungiSottomissione(sottomissione);
        repositoryIscrizioniTeam.save(iscrizioneTeam);
        for (MembroTeam m : team.getMembri())
            if (!m.equals(membro))
                servizioNotifiche.creaNotifica(m.getUtente(), TipoNotifica.SOTTOMISSIONE_MODIFICATA, membro.getUtente().getNomeUtente() + " ha modificato la sottomissione dell'hackathon " + hackathon.getNome());
    }

    /**
     * Metodo che rimuove una sottomissione di un team
     *
     * @param nomeUtente    il nome utente del membro che attiva la rimozione della sottomissione
     * @param nomeHackathon il nome dell'hackathon
     */
    @Transactional
    public void attivaRimozioneSottomissione(String nomeUtente, String nomeHackathon) {
        MembroTeam membro = validaAutorizzazione(nomeUtente);
        Team team = membro.getTeam();
        IscrizioneTeam iscrizioneTeam = repositoryIscrizioniTeam.findByTeam(team)
                .orElseThrow(() -> new NotFoundException("Team non trovato"));
        Hackathon hackathon = checkStessoHackathon(nomeHackathon, iscrizioneTeam);
        hackathon.getStato().verificaInvioSottomissioneConsentito(iscrizioneTeam.getHackathon());
        if (iscrizioneTeam.getSottomissione() == null)
            throw new ConflictException("Non è presente nessuna sottomissione da rimuovere");
        iscrizioneTeam.rimuoviSottomissione();
        repositoryIscrizioniTeam.save(iscrizioneTeam);
        for (MembroTeam m : team.getMembri())
            if (!m.equals(membro))
                servizioNotifiche.creaNotifica(m.getUtente(), TipoNotifica.SOTTOMISSIONE_RIMOSSA, membro.getUtente().getNomeUtente() + " ha attivato la rimozione della sottomissione dell'hackathon " + hackathon.getNome());
    }

    private Hackathon checkStessoHackathon(String nomeHackathon, IscrizioneTeam iscrizioneTeam) {
        Hackathon hackathon = repositoryHackathon.findByNome(nomeHackathon).orElseThrow(() -> new NotFoundException("Hackathon non trovato"));
        if (!hackathon.getIscrizioni().contains(iscrizioneTeam))
            throw new NotFoundException("Il team non è iscritto all'hackathon");
        return hackathon;
    }

    private MembroTeam validaAutorizzazione(String nomeUtente) {
        return repositoryMembriTeam.findByUtente_NomeUtente(nomeUtente).orElseThrow(() -> new NotFoundException("L'utente non è un membro di un team"));
    }
}
