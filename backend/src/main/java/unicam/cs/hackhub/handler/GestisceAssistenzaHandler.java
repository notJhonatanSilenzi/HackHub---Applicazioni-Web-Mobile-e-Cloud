package unicam.cs.hackhub.handler;

import unicam.cs.hackhub.domain.RuoloStaff;
import unicam.cs.hackhub.domain.RuoloTeam;
import unicam.cs.hackhub.domain.implementazione.Hackathon;
import unicam.cs.hackhub.domain.implementazione.MembroTeam;
import unicam.cs.hackhub.domain.implementazione.Staff;
import unicam.cs.hackhub.eccezioni.BadRequestException;
import unicam.cs.hackhub.eccezioni.ConflictException;
import unicam.cs.hackhub.eccezioni.NotFoundException;
import unicam.cs.hackhub.repository.RepositoryHackathon;
import unicam.cs.hackhub.repository.RepositoryIscrizioniTeam;
import unicam.cs.hackhub.repository.RepositoryMembriTeam;
import unicam.cs.hackhub.repository.RepositoryStaff;
import unicam.cs.hackhub.servizi.ServizioNotifiche;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import static unicam.cs.hackhub.domain.TipoNotifica.ASSISTENZA;

@Service
public class GestisceAssistenzaHandler {

    private final RepositoryMembriTeam repositoryMembriTeam;
    private final RepositoryIscrizioniTeam repositoryIscrizioniTeam;
    private final RepositoryHackathon repositoryHackathon;
    private final RepositoryStaff repositoryStaff;
    private final ServizioNotifiche servizioNotifiche;

    /**
     * Crea un istanza dell'handler
     *
     * @param repositoryMembriTeam     la repository dei membri de team
     * @param repositoryIscrizioniTeam la repository delle iscrizioni
     * @param repositoryHackathon      la repository degli hackathon
     * @param repositoryStaff          la repository dello staff
     * @param servizioNotifiche        il servizio per le notifiche
     */
    public GestisceAssistenzaHandler(RepositoryMembriTeam repositoryMembriTeam, RepositoryIscrizioniTeam repositoryIscrizioniTeam, RepositoryHackathon repositoryHackathon, RepositoryStaff repositoryStaff, ServizioNotifiche servizioNotifiche) {
        this.repositoryMembriTeam = repositoryMembriTeam;
        this.repositoryIscrizioniTeam = repositoryIscrizioniTeam;
        this.repositoryHackathon = repositoryHackathon;
        this.repositoryStaff = repositoryStaff;
        this.servizioNotifiche = servizioNotifiche;
    }

    /**
     * Permette al leader del team di richiedere assistenza ad un mentore associato all'hackathon a cui il team è iscritto
     *
     * @param nomeUtente    del leader del team che richiede assistenza
     * @param nomeMentore   il mentore a cui si vuole chiedere assistenza
     * @param nomeHackathon l'hackathon associato
     */
    @Transactional
    public void chiediAssistenza(String nomeUtente, String nomeMentore, String nomeHackathon) {
        MembroTeam leader = repositoryMembriTeam.findByUtente_NomeUtente(nomeUtente).orElseThrow(() -> new ConflictException("L'utente non è membro di alcun team."));
        if (leader.getRuolo() != RuoloTeam.LEADER) {
            throw new ConflictException("Solo il leader del team può richiedere assistenza.");
        }
        Hackathon hackathon = repositoryHackathon.findByNome(nomeHackathon).orElseThrow(() -> new BadRequestException("Hackathon non trovato."));
        if (repositoryIscrizioniTeam.findByTeamAndHackathon(leader.getTeam(), hackathon).isEmpty()) {
            throw new ConflictException("Il team non è iscritto all'hackathon.");
        }
        Staff mentore = repositoryStaff.getStaffByUtente_NomeUtente(nomeMentore).stream().filter(s -> s.getRuolo() == RuoloStaff.MENTORE).findFirst().orElseThrow(() -> new NotFoundException("L'utente selezionato non è un mentore"));
        if (!hackathon.equals(mentore.getHackathon())) {
            throw new ConflictException("Il mentore selezionato non è associato all'hackathon.");
        }
        servizioNotifiche.creaNotifica(mentore.getUtente(), ASSISTENZA, "Richiesta di assistenza");
    }
}
