package unicam.cs.hackhub.testHttp;

import unicam.cs.hackhub.domain.RuoloStaff;
import unicam.cs.hackhub.domain.RuoloTeam;
import unicam.cs.hackhub.domain.implementazione.Hackathon;
import unicam.cs.hackhub.domain.implementazione.InvitoStaff;
import unicam.cs.hackhub.domain.implementazione.IscrizioneTeam;
import unicam.cs.hackhub.domain.implementazione.MembroTeam;
import unicam.cs.hackhub.domain.implementazione.Notifica;
import unicam.cs.hackhub.domain.implementazione.Periodo;
import unicam.cs.hackhub.domain.implementazione.Richiesta;
import unicam.cs.hackhub.domain.implementazione.Staff;
import unicam.cs.hackhub.domain.implementazione.Team;
import unicam.cs.hackhub.domain.implementazione.Utente;
import unicam.cs.hackhub.domain.implementazione.statePattern.Concluso;
import unicam.cs.hackhub.domain.implementazione.statePattern.InCorso;
import unicam.cs.hackhub.domain.implementazione.statePattern.IscrizioniAperte;
import unicam.cs.hackhub.domain.implementazione.statePattern.StatoHackathon;
import unicam.cs.hackhub.repository.RepositoryHackathon;
import unicam.cs.hackhub.repository.RepositoryIscrizioniTeam;
import unicam.cs.hackhub.repository.RepositoryNotifica;
import unicam.cs.hackhub.repository.RepositoryRichiesta;
import unicam.cs.hackhub.repository.RepositoryStaff;
import unicam.cs.hackhub.repository.RepositoryTeam;
import unicam.cs.hackhub.repository.RepositoryUtente;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GestisceHackathonBoundaryIT extends BaseHttpIT {


    private static final String BASE_URL = "/api/hackathon";
    private static final String NOME_UTENTE = "organizzatore";
    private static final String NOME_MENTORE = "mentore";


    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private RepositoryHackathon repositoryHackathon;


    @Autowired
    private RepositoryStaff repositoryStaff;


    @Autowired
    private RepositoryTeam repositoryTeam;


    @Autowired
    private RepositoryUtente repositoryUtente;


    @Autowired
    private RepositoryIscrizioniTeam repositoryIscrizioniTeam;


    @Autowired
    private RepositoryNotifica repositoryNotifica;


    @Autowired
    private RepositoryRichiesta repositoryRichiesta;


    @Autowired
    private EntityManager entityManager;


    @BeforeEach
    void setUp() {
        repositoryNotifica.deleteAll();
        repositoryRichiesta.deleteAll();
        repositoryStaff.deleteAll();
        repositoryIscrizioniTeam.deleteAll();
        repositoryTeam.deleteAll();
        repositoryHackathon.deleteAll();
        repositoryUtente.deleteAll();
        entityManager.flush();
    }


    @Test
    void segnalaViolazione_ok() throws Exception {
        Utente organizzatore = salvaUtente(NOME_UTENTE);
        Utente mentore = salvaUtente(NOME_MENTORE);
        Utente membro = salvaUtente("membro1");


        Team team = salvaTeam("team-violazione", membro);


        Hackathon hackathon = creaHackathon("hackathon-violazione");
        hackathon.aggiungiStaff(new Staff(organizzatore, RuoloStaff.ORGANIZZATORE));
        hackathon.aggiungiStaff(new Staff(mentore, RuoloStaff.MENTORE));
        hackathon.aggiungiIscrizione(new IscrizioneTeam(team, hackathon));
        impostaStato(hackathon, InCorso.INSTANCE);
        repositoryHackathon.saveAndFlush(hackathon);


        mockMvc.perform(post(BASE_URL + "/{nomeHackathon}/violazione", hackathon.getNome())
                        .with(auth(NOME_MENTORE))
                        .param("nomeTeam", team.getNome()))
                .andExpect(status().isOk());


        List<Notifica> notifiche = repositoryNotifica.findAll();
        assertEquals(1, notifiche.size());
        assertEquals(NOME_UTENTE, notifiche.get(0).getDestinatario().getNomeUtente());
        assertTrue(notifiche.get(0).getPayload().contains(team.getNome()));
    }


    @Test
    void segnalaViolazione_notFound() throws Exception {
        Utente organizzatore = salvaUtente(NOME_UTENTE);
        Utente mentore = salvaUtente(NOME_MENTORE);


        Hackathon hackathon = creaHackathon("hackathon-violazione-notfound");
        hackathon.aggiungiStaff(new Staff(organizzatore, RuoloStaff.ORGANIZZATORE));
        hackathon.aggiungiStaff(new Staff(mentore, RuoloStaff.MENTORE));
        impostaStato(hackathon, InCorso.INSTANCE);
        repositoryHackathon.saveAndFlush(hackathon);


        mockMvc.perform(post(BASE_URL + "/{nomeHackathon}/violazione", hackathon.getNome())
                        .with(auth(NOME_MENTORE))
                        .param("nomeTeam", "team-inesistente"))
                .andExpect(status().isNotFound());
    }


    @Test
    void nominaMentori_ok() throws Exception {
        Utente organizzatore = salvaUtente(NOME_UTENTE);
        Utente invitato = salvaUtente("utenteDaInvitare");


        Hackathon hackathon = creaHackathon("hackathon-nomina");
        hackathon.aggiungiStaff(new Staff(organizzatore, RuoloStaff.ORGANIZZATORE));
        impostaStato(hackathon, IscrizioniAperte.INSTANCE);
        repositoryHackathon.saveAndFlush(hackathon);


        mockMvc.perform(post(BASE_URL + "/{nomeHackathon}/nomine-mentori", hackathon.getNome())
                        .with(auth())
                        .param("nomeUtenteDaInvitare", invitato.getNomeUtente()))
                .andExpect(status().isOk());


        List<Richiesta> richieste = repositoryRichiesta.findAll();
        assertEquals(1, richieste.size());
        assertInstanceOf(InvitoStaff.class, richieste.get(0));


        InvitoStaff invitoStaff = (InvitoStaff) richieste.get(0);
        assertEquals(invitato.getNomeUtente(), invitoStaff.getDestinatario().getNomeUtente());
        assertEquals(RuoloStaff.MENTORE, invitoStaff.getRuolo());
        assertEquals(hackathon.getNome(), invitoStaff.getHackathon().getNome());
    }


    @Test
    void nominaMentori_errore() throws Exception {
        Utente organizzatore = salvaUtente(NOME_UTENTE);
        Utente invitato = salvaUtente("utenteDaInvitare");


        Hackathon hackathon = creaHackathon("hackathon-nomina-errore");
        hackathon.aggiungiStaff(new Staff(organizzatore, RuoloStaff.ORGANIZZATORE));
        impostaStato(hackathon, InCorso.INSTANCE);
        repositoryHackathon.saveAndFlush(hackathon);


        mockMvc.perform(post(BASE_URL + "/{nomeHackathon}/nomine-mentori", hackathon.getNome())
                        .with(auth())
                        .param("nomeUtenteDaInvitare", invitato.getNomeUtente()))
                .andExpect(status().isConflict());
    }


    @Test
    void eliminaHackathon_ok() throws Exception {
        Utente organizzatore = salvaUtente(NOME_UTENTE);
        Utente membro = salvaUtente("membro-cancellazione");


        Team team = salvaTeam("team-cancellazione", membro);


        Hackathon hackathon = creaHackathon("hackathon-cancellazione");
        hackathon.aggiungiStaff(new Staff(organizzatore, RuoloStaff.ORGANIZZATORE));
        hackathon.aggiungiIscrizione(new IscrizioneTeam(team, hackathon));
        impostaStato(hackathon, IscrizioniAperte.INSTANCE);
        repositoryHackathon.saveAndFlush(hackathon);


        mockMvc.perform(delete(BASE_URL + "/{nomeHackathon}", hackathon.getNome())
                        .with(auth()))
                .andExpect(status().isOk());


        assertTrue(repositoryHackathon.findByNome(hackathon.getNome()).isEmpty());


        List<Notifica> notifiche = repositoryNotifica.findAll();
        assertEquals(1, notifiche.size());
        assertEquals("membro-cancellazione", notifiche.get(0).getDestinatario().getNomeUtente());
        assertTrue(notifiche.get(0).getPayload().contains("cancellato"));
    }


    @Test
    void eliminaHackathon_errore() throws Exception {
        Utente organizzatore = salvaUtente(NOME_UTENTE);


        Hackathon hackathon = creaHackathon("hackathon-cancellazione-errore");
        hackathon.aggiungiStaff(new Staff(organizzatore, RuoloStaff.ORGANIZZATORE));
        impostaStato(hackathon, Concluso.INSTANCE);
        repositoryHackathon.saveAndFlush(hackathon);


        mockMvc.perform(delete(BASE_URL + "/{nomeHackathon}", hackathon.getNome())
                        .with(auth()))
                .andExpect(status().isConflict());
    }


    @Test
    void espelliTeam_ok() throws Exception {
        Utente organizzatore = salvaUtente(NOME_UTENTE);
        Utente membro1 = salvaUtente("membro-espulsione-1");
        Utente membro2 = salvaUtente("membro-espulsione-2");


        Team team = salvaTeam("team-espulsione", membro1, membro2);


        Hackathon hackathon = creaHackathon("hackathon-espulsione");
        hackathon.aggiungiStaff(new Staff(organizzatore, RuoloStaff.ORGANIZZATORE));
        hackathon.aggiungiIscrizione(new IscrizioneTeam(team, hackathon));
        impostaStato(hackathon, InCorso.INSTANCE);
        repositoryHackathon.saveAndFlush(hackathon);


        mockMvc.perform(post(BASE_URL + "/{nomeHackathon}/team/{nomeTeam}/espulsione",
                        hackathon.getNome(), team.getNome())
                        .with(auth()))
                .andExpect(status().isOk());


        assertTrue(repositoryIscrizioniTeam.findByTeamAndHackathon(team, hackathon).isEmpty());


        List<Notifica> notifiche = repositoryNotifica.findAll();
        assertEquals(2, notifiche.size());
        assertTrue(notifiche.stream().allMatch(n -> n.getPayload().contains("espulso")));
    }


    @Test
    void espelliTeam_notFound() throws Exception {
        Utente organizzatore = salvaUtente(NOME_UTENTE);
        Utente membro = salvaUtente("membro-team-non-iscritto");


        Team team = salvaTeam("team-non-iscritto", membro);


        Hackathon hackathon = creaHackathon("hackathon-espulsione-notfound");
        hackathon.aggiungiStaff(new Staff(organizzatore, RuoloStaff.ORGANIZZATORE));
        impostaStato(hackathon, InCorso.INSTANCE);
        repositoryHackathon.saveAndFlush(hackathon);


        mockMvc.perform(post(BASE_URL + "/{nomeHackathon}/team/{nomeTeam}/espulsione",
                        hackathon.getNome(), team.getNome())
                        .with(auth()))
                .andExpect(status().isNotFound());
    }


    @Test
    void proclamaVincitore_ok() throws Exception {
        Utente organizzatore = salvaUtente(NOME_UTENTE);
        Utente vincitoreMembro = salvaUtente("membro-vincitore");
        Utente sconfittoMembro = salvaUtente("membro-sconfitto");


        Team teamVincitore = salvaTeam("team-vincitore", vincitoreMembro);
        Team teamSconfitto = salvaTeam("team-sconfitto", sconfittoMembro);


        Hackathon hackathon = creaHackathon("hackathon-vincitore");
        hackathon.aggiungiStaff(new Staff(organizzatore, RuoloStaff.ORGANIZZATORE));
        hackathon.aggiungiIscrizione(new IscrizioneTeam(teamVincitore, hackathon));
        hackathon.aggiungiIscrizione(new IscrizioneTeam(teamSconfitto, hackathon));
        impostaStato(hackathon, Concluso.INSTANCE);
        repositoryHackathon.saveAndFlush(hackathon);


        mockMvc.perform(post(BASE_URL + "/{nomeHackathon}/vincitore", hackathon.getNome())
                        .with(auth())
                        .param("nomeTeam", teamVincitore.getNome()))
                .andExpect(status().isOk());


        List<Notifica> notifiche = repositoryNotifica.findAll();
        assertEquals(2, notifiche.size());
        assertTrue(notifiche.stream().anyMatch(n ->
                n.getDestinatario().getNomeUtente().equals("membro-vincitore")
                        && n.getPayload().contains("ha vinto")));
        assertTrue(notifiche.stream().anyMatch(n ->
                n.getDestinatario().getNomeUtente().equals("membro-sconfitto")
                        && n.getPayload().contains("non ha vinto")));
    }


    @Test
    void proclamaVincitore_errore() throws Exception {
        Utente organizzatore = salvaUtente(NOME_UTENTE);
        Utente membro = salvaUtente("membro-team");


        Team team = salvaTeam("team-non-concluso", membro);


        Hackathon hackathon = creaHackathon("hackathon-non-concluso");
        hackathon.aggiungiStaff(new Staff(organizzatore, RuoloStaff.ORGANIZZATORE));
        hackathon.aggiungiIscrizione(new IscrizioneTeam(team, hackathon));
        impostaStato(hackathon, InCorso.INSTANCE);
        repositoryHackathon.saveAndFlush(hackathon);


        mockMvc.perform(post(BASE_URL + "/{nomeHackathon}/vincitore", hackathon.getNome())
                        .with(auth())
                        .param("nomeTeam", team.getNome()))
                .andExpect(status().isConflict());
    }


    @Test
    void attivaLiquidazionePremio_ok() throws Exception {
        Utente organizzatore = salvaUtenteConIban(NOME_UTENTE, "IT00A0000000000000000000001");
        Utente membro1 = salvaUtenteConIban("membro-premio-1", "IT00A0000000000000000000002");
        Utente membro2 = salvaUtenteConIban("membro-premio-2", "IT00A0000000000000000000003");


        Team team = salvaTeam("team-premio", membro1, membro2);


        Hackathon hackathon = creaHackathon("hackathon-premio");
        hackathon.aggiungiStaff(new Staff(organizzatore, RuoloStaff.ORGANIZZATORE));
        hackathon.aggiungiIscrizione(new IscrizioneTeam(team, hackathon));
        impostaStato(hackathon, Concluso.INSTANCE);
        repositoryHackathon.saveAndFlush(hackathon);


        mockMvc.perform(post(BASE_URL + "/{nomeHackathon}/liquidazione-premio", hackathon.getNome())
                        .with(auth())
                        .param("nomeTeam", team.getNome()))
                .andExpect(status().isOk());
    }


    @Test
    void attivaLiquidazionePremio_notFound() throws Exception {
        Utente organizzatore = salvaUtenteConIban(NOME_UTENTE, "IT00A0000000000000000000010");
        Utente membro = salvaUtenteConIban("membro-senza-iscrizione", "IT00A0000000000000000000011");


        Team team = salvaTeam("team-senza-iscrizione", membro);


        Hackathon hackathon = creaHackathon("hackathon-premio-notfound");
        hackathon.aggiungiStaff(new Staff(organizzatore, RuoloStaff.ORGANIZZATORE));
        impostaStato(hackathon, Concluso.INSTANCE);
        repositoryHackathon.saveAndFlush(hackathon);


        mockMvc.perform(post(BASE_URL + "/{nomeHackathon}/liquidazione-premio", hackathon.getNome())
                        .with(auth())
                        .param("nomeTeam", team.getNome()))
                .andExpect(status().isNotFound());
    }


    private RequestPostProcessor auth() {
        return auth(NOME_UTENTE);
    }


    private RequestPostProcessor auth(String nomeUtente) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        nomeUtente,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );
        return SecurityMockMvcRequestPostProcessors.authentication(authentication);
    }


    private Utente salvaUtente(String nomeUtente) {
        Utente utente = new Utente(nomeUtente, nomeUtente + "@mail.it", "password");
        return repositoryUtente.saveAndFlush(utente);
    }


    private Utente salvaUtenteConIban(String nomeUtente, String iban) {
        Utente utente = new Utente(nomeUtente, nomeUtente + "@mail.it", "password");
        utente.setRecapitoBancario(iban);
        return repositoryUtente.saveAndFlush(utente);
    }


    private Team salvaTeam(String nomeTeam, Utente... membri) {
        Team team = new Team(nomeTeam);
        for (Utente utente : membri) {
            team.getMembri().add(new MembroTeam(utente, team, RuoloTeam.MEMBRO));
        }
        return repositoryTeam.saveAndFlush(team);
    }


    private Hackathon creaHackathon(String nomeHackathon) {
        return new Hackathon(
                nomeHackathon,
                new Periodo(
                        LocalDate.now().plusDays(10),
                        LocalTime.of(9, 0),
                        LocalDate.now().plusDays(12),
                        LocalTime.of(18, 0)
                ),
                new BigDecimal("1000.00"),
                "Camerino",
                6,
                3,
                LocalDateTime.now().plusDays(5),
                "Regolamento di test",
                10
        );
    }


    private void impostaStato(Hackathon hackathon, StatoHackathon stato) {
        hackathon.setStato(stato);
        hackathon.setStatoEnum(stato);
    }
}


