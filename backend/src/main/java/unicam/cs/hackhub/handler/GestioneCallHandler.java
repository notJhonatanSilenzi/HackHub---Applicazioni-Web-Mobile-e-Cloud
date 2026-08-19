package unicam.cs.hackhub.handler;

import unicam.cs.hackhub.boundary.dto.PropostaCallRequest;
import unicam.cs.hackhub.domain.RuoloStaff;
import unicam.cs.hackhub.domain.RuoloTeam;
import unicam.cs.hackhub.domain.implementazione.*;
import unicam.cs.hackhub.servizi.ServizioNotifiche;
import unicam.cs.hackhub.eccezioni.ConflictException;
import unicam.cs.hackhub.eccezioni.ForbiddenException;
import unicam.cs.hackhub.eccezioni.NotFoundException;
import unicam.cs.hackhub.repository.RepositoryHackathon;
import unicam.cs.hackhub.repository.RepositoryMembriTeam;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class GestioneCallHandler {

    private final RepositoryMembriTeam repositoryMembriTeam;
    private final RepositoryHackathon repositoryHackathon;
    private final ServizioNotifiche servizioNotifiche;

    /**
     * Costruttore per GestioneCallHandler, che riceve in input i repository necessari per gestire le call proposte dai
     * mentori.
     *
     * @param repositoryMembroTeam la repository per recuperare i membri del team e verificare che l'utente che propone
     *                             la call sia un mentore autorizzato
     * @param repositoryHackathon  la repository per recuperare l'hackathon a cui è iscritto il team e verificare che
     *                             la call sia proposta prima della fine dell'hackathon
     * @param servizioNotifiche    il servizio per inviare le notifiche al leader del team quando viene proposta una call
     */
    public GestioneCallHandler(RepositoryMembriTeam repositoryMembroTeam, RepositoryHackathon repositoryHackathon, ServizioNotifiche servizioNotifiche) {
        this.repositoryMembriTeam = repositoryMembroTeam;
        this.repositoryHackathon = repositoryHackathon;
        this.servizioNotifiche = servizioNotifiche;
    }

    /**
     * Avvia una proposta di call da un mentore a un team, la durata della call è fissa a 30 minuti.
     *
     * @param nomeUtente il nome utente del mentore che propone la call
     * @param request    la richiesta contenente tutti i dettagli
     */
    @Transactional
    public void avviaPropostaCall(String nomeUtente, PropostaCallRequest request) {
        Hackathon hackathon = repositoryHackathon.findByNome(request.nomeHackathon()).orElseThrow(() ->
                new NotFoundException("Hackathon non esistente"));
        verificaMentoreAutorizzato(hackathon, nomeUtente);
        Periodo periodo = new Periodo(request.data(), request.ora(), request.data(), request.ora().plusMinutes(30));
        validazione(periodo, hackathon, request.nomeTeam());
        Team team = hackathon.getIscrizioni().stream()
                .map(IscrizioneTeam::getTeam)
                .filter(t -> t.getNome().equals(request.nomeTeam()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Team non trovato nell'hackathon"));
        Utente leader = repositoryMembriTeam.findByTeam_IdTeamAndRuolo(team.getIdTeam(), RuoloTeam.LEADER)
                .map(MembroTeam::getUtente)
                .orElseThrow(() -> new NotFoundException("Leader del team non trovato"));
        servizioNotifiche.creaPropostaCall(nomeUtente, leader, periodo);
    }

    /**
     * Controlla che sia possibile effettuare call
     *
     * @param periodo   il periodo dell'hackathon
     * @param hackathon l'hackathon
     * @param nomeTeam  il nome del Team
     */
    private void validazione(Periodo periodo, Hackathon hackathon, String nomeTeam) {
        hackathon.getStato().verificaPropostaDiCallConsentita(hackathon);
        if (hackathon.getIscrizioni().stream().noneMatch(i -> i.getTeam().getNome().equals(nomeTeam))) {
            throw new ConflictException("Il team non è iscritto all'hackathon");
        }
        if (periodo.getDataFine().isAfter(hackathon.getPeriodo().getDataFine())) {
            throw new ConflictException("La call non può essere dopo la fine dell'hackathon");
        }
    }

    /**
     * Verifica che l'utente che vuole inviare le call sia un mentore dell'hackathon
     *
     * @param hackathon   l'hackathon
     * @param nomeMentore il nome del mentore che vuole inviare la call
     */
    private void verificaMentoreAutorizzato(Hackathon hackathon, String nomeMentore) {
        boolean autorizzato = hackathon.getStaff().stream()
                .anyMatch(s -> s.getRuolo() == RuoloStaff.MENTORE && s.getUtente().getNomeUtente().equals(nomeMentore));
        if (!autorizzato) {
            throw new ForbiddenException("Utente non autorizzato a inviare call");
        }
    }
}
