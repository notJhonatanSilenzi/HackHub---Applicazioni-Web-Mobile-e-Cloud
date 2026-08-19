package unicam.cs.hackhub.handler;

import unicam.cs.hackhub.boundary.dto.ValutazioneRequest;
import unicam.cs.hackhub.domain.RuoloStaff;
import unicam.cs.hackhub.domain.TipoNotifica;
import unicam.cs.hackhub.domain.implementazione.*;
import unicam.cs.hackhub.domain.implementazione.*;
import unicam.cs.hackhub.eccezioni.*;
import unicam.cs.hackhub.repository.*;
import unicam.cs.hackhub.eccezioni.ConflictException;
import unicam.cs.hackhub.eccezioni.ForbiddenException;
import unicam.cs.hackhub.eccezioni.NotFoundException;
import unicam.cs.hackhub.eccezioni.TransizioneNonConsentitaException;
import unicam.cs.hackhub.repository.*;
import unicam.cs.hackhub.servizi.ServizioNotifiche;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class ValutazioneHandler {

    private final RepositorySottomissioni repositorySottomissioni;
    private final RepositoryHackathon repositoryHackathon;
    private final RepositoryStaff repositoryStaff;
    private final ServizioNotifiche servizioNotifiche;
    private final RepositoryIscrizioniTeam repositoryIscrizioniTeam;
    private final RepositoryValutazioni repositoryValutazioni;

    /**
     * Crea una nuova istanza di un handler per la valutazione delle sottomissioni
     *
     * @param repositorySottomissioni la repository delle sottomissioni
     * @param repositoryHackathon     la repository degli hackathon
     * @param repositoryStaff         la repository dello staff
     * @param servizioNotifiche       il servizio per le notifiche
     */
    public ValutazioneHandler(RepositorySottomissioni repositorySottomissioni, RepositoryHackathon repositoryHackathon,
                              RepositoryStaff repositoryStaff, ServizioNotifiche servizioNotifiche, RepositoryIscrizioniTeam repositoryIscrizioniTeam, RepositoryValutazioni repositoryValutazioni) {
        this.repositorySottomissioni = repositorySottomissioni;
        this.repositoryHackathon = repositoryHackathon;
        this.repositoryStaff = repositoryStaff;
        this.servizioNotifiche = servizioNotifiche;
        this.repositoryIscrizioniTeam = repositoryIscrizioniTeam;
        this.repositoryValutazioni = repositoryValutazioni;
    }

    /**
     * Avvia l'inserimento di una nuova valutazione per una sottomissione
     *
     * @param idSottomissione l'id della sottomissione da valutare
     * @param nomeUtente      il nome utente del giudice che sta valutando la sottomissione
     * @param request         la valutazione con i suoi componenti
     */
    @Transactional
    public void avviaInserimentoValutazione(String idSottomissione, String nomeUtente, ValutazioneRequest request) {
        Sottomissione sottomissione = repositorySottomissioni.findById(idSottomissione).orElseThrow(() ->
                new NotFoundException("Sottomissione non trovata"));
        Hackathon hackathon = repositoryStaff.findByUtente_NomeUtente(nomeUtente).orElseThrow(() ->
                new NotFoundException("Giudice non trovato")).getHackathon();

        boolean appartieneHackathon = repositoryIscrizioniTeam.findAllByHackathon(hackathon).stream()
                .map(IscrizioneTeam::getSottomissione)
                .filter(Objects::nonNull)
                .anyMatch(s -> s.getIdSottomissione().equals(idSottomissione));
        if (!appartieneHackathon) {
            throw new ForbiddenException("Sottomissione non appartenente all'hackathon del giudice");
        }

        try {
            hackathon.getStato().verificaValutazioneConsentita(hackathon);
        } catch (TransizioneNonConsentitaException ex) {
            throw new ConflictException("Valutazione non consentita in questo stato dell'hackathon");
        }
        verificaGiudiceAutorizzato(hackathon, nomeUtente);
        creaOAggiornaValutazione(sottomissione, request.punteggio(), request.giudizio());
        repositorySottomissioni.save(sottomissione);
        concludiHackathonSeTutteValutate(hackathon);
    }

    /**
     * Verifica che l'utente che vuole valutare le sottomissioni sia il giudice dell'hackathon
     *
     * @param hackathon  l'hackathon
     * @param nomeUtente il nome utente dell'utente che vuole valutare
     */
    private void verificaGiudiceAutorizzato(Hackathon hackathon, String nomeUtente) {
        boolean autorizzato = hackathon.getStaff().stream()
                .anyMatch(s -> s.getRuolo() == RuoloStaff.GIUDICE && s.getUtente().getNomeUtente().equals(nomeUtente));
        if (!autorizzato) {
            throw new ForbiddenException("Utente non autorizzato a valutare questa sottomissione");
        }
    }

    /**
     * Crea o aggiorna una valutazione per una sottomissione
     *
     * @param sottomissione la sottomissione da valutare
     * @param punteggio     il punteggio associato
     * @param giudizio      il giudizio associato
     */
    private void creaOAggiornaValutazione(Sottomissione sottomissione, int punteggio, String giudizio) {
        Valutazione valutazione = sottomissione.getValutazione();

        if (valutazione == null) {
            valutazione = new Valutazione(punteggio, giudizio);
            sottomissione.impostaValutazione(valutazione);
            repositoryValutazioni.save(valutazione);
        } else {
            valutazione.setVoto(punteggio);
            valutazione.setDescrizione(giudizio);
            repositoryValutazioni.save(valutazione);
        }
    }

    /**
     * Conclude l'hackathon se tutte le sottomissioni sono state valutate
     * @param hackathon l'hackathon
     */
    private void concludiHackathonSeTutteValutate(Hackathon hackathon) {
        List<Sottomissione> sottomissioni = repositoryIscrizioniTeam.findAllByHackathon(hackathon).stream().
                map(IscrizioneTeam::getSottomissione).filter(Objects::nonNull).toList();
        boolean tutteValutate = sottomissioni.stream().allMatch(Sottomissione::haValutazione);
        if (tutteValutate) {
            hackathon.concludi();
            repositoryHackathon.save(hackathon);
            String messaggio = "L'hackathon è stato concluso, valutazioni terminate";
            List<Utente> utentiDestinatari = getUtentiDestinatari(hackathon);
            for (Utente u : utentiDestinatari)
                servizioNotifiche.creaNotifica(u, TipoNotifica.VALUTAZIONE_CONCLUSA, messaggio);
        }
    }

    private List<Utente> getUtentiDestinatari(Hackathon hackathon){
        return hackathon.getIscrizioni().stream()
                .filter(i -> i.getHackathon().equals(hackathon))
                .map(IscrizioneTeam::getTeam)
                .flatMap(team -> team.getMembri().stream())
                .map(MembroTeam::getUtente)
                .toList();
    }

}
