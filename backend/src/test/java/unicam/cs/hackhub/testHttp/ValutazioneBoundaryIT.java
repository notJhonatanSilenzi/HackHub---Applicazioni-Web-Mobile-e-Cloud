package unicam.cs.hackhub.testHttp;


import unicam.cs.hackhub.domain.RuoloStaff;
import unicam.cs.hackhub.domain.RuoloTeam;
import unicam.cs.hackhub.domain.StatoEnum;
import unicam.cs.hackhub.domain.TipoNotifica;
import unicam.cs.hackhub.domain.implementazione.*;
import unicam.cs.hackhub.domain.implementazione.*;
import unicam.cs.hackhub.domain.implementazione.statePattern.ValutazioneInCorso;
import unicam.cs.hackhub.repository.*;
import unicam.cs.hackhub.repository.*;
import unicam.cs.hackhub.servizi.ServizioNotifiche;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ValutazioneBoundaryIT extends BaseHttpIT {


    private static final String ENDPOINT = "/api/sottomissioni";
    private static final String GIUDICE = "giudice_user";
    private static final String MENTORE = "mentore_user";
    private static final String OUTSIDER = "outsider_user";
    private static final String LEADER_1 = "leader_one";
    private static final String LEADER_2 = "leader_two";


    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private RepositoryUtente repositoryUtente;


    @Autowired
    private RepositoryStaff repositoryStaff;


    @Autowired
    private RepositoryHackathon repositoryHackathon;


    @Autowired
    private RepositoryTeam repositoryTeam;


    @Autowired
    private RepositoryMembriTeam repositoryMembriTeam;


    @Autowired
    private RepositoryIscrizioniTeam repositoryIscrizioniTeam;


    @Autowired
    private RepositorySottomissioni repositorySottomissioni;


    @Autowired
    private RepositoryValutazioni repositoryValutazioni;


    @Autowired
    private RepositoryNotifica repositoryNotifica;


    @MockitoSpyBean
    private ServizioNotifiche servizioNotifiche;


    @BeforeEach
    void setUp() {
        repositoryNotifica.deleteAllInBatch();
        repositoryStaff.deleteAllInBatch();
        repositoryIscrizioniTeam.deleteAllInBatch();
        repositoryMembriTeam.deleteAllInBatch();
        repositoryTeam.deleteAllInBatch();
        repositorySottomissioni.deleteAllInBatch();
        repositoryValutazioni.deleteAllInBatch();
        repositoryHackathon.deleteAllInBatch();
        repositoryUtente.deleteAllInBatch();


        repositoryNotifica.flush();
        repositoryStaff.flush();
        repositoryIscrizioniTeam.flush();
        repositoryMembriTeam.flush();
        repositoryTeam.flush();
        repositorySottomissioni.flush();
        repositoryValutazioni.flush();
        repositoryHackathon.flush();
        repositoryUtente.flush();


        repositoryUtente.saveAndFlush(creaUtente(GIUDICE));
        repositoryUtente.saveAndFlush(creaUtente(MENTORE));
        repositoryUtente.saveAndFlush(creaUtente(OUTSIDER));
        repositoryUtente.saveAndFlush(creaUtente(LEADER_1));
        repositoryUtente.saveAndFlush(creaUtente(LEADER_2));
    }


    @Test
    void inserisciValutazione_nuova_ok() throws Exception {
        Hackathon hackathon = creaHackathon("HackValutazione-1");
        Team teamA = creaTeamConLeader("TeamA", LEADER_1);
        Team teamB = creaTeamConLeader("TeamB", LEADER_2);


        IscrizioneTeam iscrizioneA = new IscrizioneTeam(teamA, hackathon);
        iscrizioneA.aggiungiSottomissione(new Sottomissione("https://repo/a"));
        IscrizioneTeam iscrizioneB = new IscrizioneTeam(teamB, hackathon);
        iscrizioneB.aggiungiSottomissione(new Sottomissione("https://repo/b"));


        hackathon.aggiungiIscrizione(iscrizioneA);
        hackathon.aggiungiIscrizione(iscrizioneB);
        impostaStatoValutazione(hackathon);
        repositoryHackathon.saveAndFlush(hackathon);


        String idSottomissione = iscrizioneA.getSottomissione().getIdSottomissione();


        mockMvc.perform(post(ENDPOINT + "/{id}/valutazione", idSottomissione)
                        .with(authentication(auth(GIUDICE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                               {"giudizio":"Ottimo progetto","punteggio":9}
                               """))
                .andExpect(status().isNoContent());


        Sottomissione salvata = repositorySottomissioni.findById(idSottomissione).orElseThrow();
        assertNotNull(salvata.getValutazione());
        assertEquals(9, salvata.getValutazione().getVoto());
        assertEquals("Ottimo progetto", salvata.getValutazione().getDescrizione());
        assertEquals(1, repositoryValutazioni.count());
    }


    @Test
    void inserisciValutazione_aggiornamentoEsistente_ok() throws Exception {
        Hackathon hackathon = creaHackathon("HackValutazione-2");
        Team teamA = creaTeamConLeader("TeamC", LEADER_1);
        Team teamB = creaTeamConLeader("TeamD", LEADER_2);


        IscrizioneTeam iscrizioneA = new IscrizioneTeam(teamA, hackathon);
        iscrizioneA.aggiungiSottomissione(new Sottomissione("https://repo/c"));
        IscrizioneTeam iscrizioneB = new IscrizioneTeam(teamB, hackathon);
        iscrizioneB.aggiungiSottomissione(new Sottomissione("https://repo/d"));


        hackathon.aggiungiIscrizione(iscrizioneA);
        hackathon.aggiungiIscrizione(iscrizioneB);
        impostaStatoValutazione(hackathon);
        repositoryHackathon.saveAndFlush(hackathon);


        Sottomissione sottomissione = iscrizioneA.getSottomissione();
        Valutazione valutazione = repositoryValutazioni.saveAndFlush(new Valutazione(4, "Da migliorare"));
        sottomissione.impostaValutazione(valutazione);
        repositorySottomissioni.saveAndFlush(sottomissione);


        String idValutazione = valutazione.getIdValutazione();


        mockMvc.perform(post(ENDPOINT + "/{id}/valutazione", sottomissione.getIdSottomissione())
                        .with(authentication(auth(GIUDICE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                               {"giudizio":"Migliorato molto","punteggio":8}
                               """))
                .andExpect(status().isNoContent());


        Sottomissione aggiornata = repositorySottomissioni.findById(sottomissione.getIdSottomissione()).orElseThrow();
        assertNotNull(aggiornata.getValutazione());
        assertEquals(idValutazione, aggiornata.getValutazione().getIdValutazione());
        assertEquals(8, aggiornata.getValutazione().getVoto());
        assertEquals("Migliorato molto", aggiornata.getValutazione().getDescrizione());
        assertEquals(1, repositoryValutazioni.count());
    }


    @Test
    void inserisciValutazione_concludeHackathon_quandoTutteValutate() throws Exception {
        Hackathon hackathon = creaHackathon("HackValutazione-3");
        Team teamA = creaTeamConLeader("TeamE", LEADER_1);
        Team teamB = creaTeamConLeader("TeamF", LEADER_2);


        IscrizioneTeam iscrizioneA = new IscrizioneTeam(teamA, hackathon);
        iscrizioneA.aggiungiSottomissione(new Sottomissione("https://repo/e"));
        IscrizioneTeam iscrizioneB = new IscrizioneTeam(teamB, hackathon);
        iscrizioneB.aggiungiSottomissione(new Sottomissione("https://repo/f"));


        hackathon.aggiungiIscrizione(iscrizioneA);
        hackathon.aggiungiIscrizione(iscrizioneB);
        impostaStatoValutazione(hackathon);
        repositoryHackathon.saveAndFlush(hackathon);


        // prima sottomissione gia valutata: la seconda deve chiudere l'hackathon
        Valutazione valutazioneGiaPresente = repositoryValutazioni.saveAndFlush(new Valutazione(7, "Buono"));
        iscrizioneA.getSottomissione().impostaValutazione(valutazioneGiaPresente);
        repositorySottomissioni.saveAndFlush(iscrizioneA.getSottomissione());


        mockMvc.perform(post(ENDPOINT + "/{id}/valutazione", iscrizioneB.getSottomissione().getIdSottomissione())
                        .with(authentication(auth(GIUDICE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                               {"giudizio":"Eccellente","punteggio":10}
                               """))
                .andExpect(status().isNoContent());


        Hackathon persisted = repositoryHackathon.findById(hackathon.getIdHackathon()).orElseThrow();
        assertEquals(StatoEnum.CONCLUSO, persisted.getStatoEnum());


        List<Notifica> notifiche = repositoryNotifica.findAll();
        assertEquals(2, notifiche.size());
        assertTrue(notifiche.stream().allMatch(n -> n.getTipoNotifica() == TipoNotifica.VALUTAZIONE_CONCLUSA));
        verify(servizioNotifiche, atLeast(2)).creaNotifica(any(), eq(TipoNotifica.VALUTAZIONE_CONCLUSA), any());
    }


    @Test
    void inserisciValutazione_sottomissioneNonTrovata_notFound() throws Exception {
        mockMvc.perform(post(ENDPOINT + "/{id}/valutazione", "S-inesistente")
                        .with(authentication(auth(GIUDICE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                               {"giudizio":"X","punteggio":5}
                               """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Sottomissione non trovata"));
    }


    @Test
    void inserisciValutazione_giudiceNonTrovato_notFound() throws Exception {
        Hackathon hackathon = creaHackathon("HackValutazione-4");
        Team team = creaTeamConLeader("TeamG", LEADER_1);
        IscrizioneTeam iscrizione = new IscrizioneTeam(team, hackathon);
        iscrizione.aggiungiSottomissione(new Sottomissione("https://repo/g"));
        hackathon.aggiungiIscrizione(iscrizione);
        impostaStatoValutazione(hackathon);
        repositoryHackathon.saveAndFlush(hackathon);


        mockMvc.perform(post(ENDPOINT + "/{id}/valutazione", iscrizione.getSottomissione().getIdSottomissione())
                        .with(authentication(auth(OUTSIDER)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                               {"giudizio":"Test","punteggio":6}
                               """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Giudice non trovato"));
    }


    @Test
    void inserisciValutazione_utenteNonGiudice_forbidden() throws Exception {
        Hackathon hackathon = creaHackathon("HackValutazione-5");
        hackathon.aggiungiStaff(new Staff(utente(MENTORE), RuoloStaff.MENTORE));


        Team team = creaTeamConLeader("TeamH", LEADER_1);
        IscrizioneTeam iscrizione = new IscrizioneTeam(team, hackathon);
        iscrizione.aggiungiSottomissione(new Sottomissione("https://repo/h"));
        hackathon.aggiungiIscrizione(iscrizione);
        impostaStatoValutazione(hackathon);
        repositoryHackathon.saveAndFlush(hackathon);


        mockMvc.perform(post(ENDPOINT + "/{id}/valutazione", iscrizione.getSottomissione().getIdSottomissione())
                        .with(authentication(auth(MENTORE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                               {"giudizio":"Test","punteggio":6}
                               """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Utente non autorizzato a valutare questa sottomissione"));
    }


    @Test
    void inserisciValutazione_statoHackathonNonValido_conflict() throws Exception {
        Hackathon hackathon = creaHackathon("HackValutazione-6");
        Team team = creaTeamConLeader("TeamI", LEADER_1);


        IscrizioneTeam iscrizione = new IscrizioneTeam(team, hackathon);
        iscrizione.aggiungiSottomissione(new Sottomissione("https://repo/i"));
        hackathon.aggiungiIscrizione(iscrizione);
        repositoryHackathon.saveAndFlush(hackathon);


        mockMvc.perform(post(ENDPOINT + "/{id}/valutazione", iscrizione.getSottomissione().getIdSottomissione())
                        .with(authentication(auth(GIUDICE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                               {"giudizio":"Test","punteggio":6}
                               """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Valutazione non consentita in questo stato dell'hackathon"));
    }


    @Test
    void inserisciValutazione_sottomissioneDiAltroHackathon_forbidden() throws Exception {
        Hackathon hackathonGiudice = creaHackathon("HackValutazione-9");
        Team teamGiudice = creaTeamConLeader("TeamN", LEADER_1);
        IscrizioneTeam iscrizioneGiudice = new IscrizioneTeam(teamGiudice, hackathonGiudice);
        iscrizioneGiudice.aggiungiSottomissione(new Sottomissione("https://repo/n"));
        hackathonGiudice.aggiungiIscrizione(iscrizioneGiudice);
        impostaStatoValutazione(hackathonGiudice);
        repositoryHackathon.saveAndFlush(hackathonGiudice);


        Hackathon altroHackathon = new Hackathon(
                "HackAltro-" + System.nanoTime(),
                new Periodo(LocalDate.now().plusDays(10), LocalDate.now().plusDays(12)),
                new BigDecimal("1000"),
                "Camerino",
                5,
                3,
                LocalDateTime.now().plusDays(5),
                "Regolamento",
                20
        );
        Team altroTeam = creaTeamConLeader("TeamO", LEADER_2);
        IscrizioneTeam altraIscrizione = new IscrizioneTeam(altroTeam, altroHackathon);
        altraIscrizione.aggiungiSottomissione(new Sottomissione("https://repo/o"));
        altroHackathon.aggiungiIscrizione(altraIscrizione);
        impostaStatoValutazione(altroHackathon);
        repositoryHackathon.saveAndFlush(altroHackathon);


        mockMvc.perform(post(ENDPOINT + "/{id}/valutazione", altraIscrizione.getSottomissione().getIdSottomissione())
                        .with(authentication(auth(GIUDICE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                               {"giudizio":"Test","punteggio":6}
                               """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Sottomissione non appartenente all'hackathon del giudice"));
    }


    @Test
    void inserisciValutazione_giudizioVuoto_badRequest() throws Exception {
        Hackathon hackathon = creaHackathon("HackValutazione-7");
        Team team = creaTeamConLeader("TeamL", LEADER_1);


        IscrizioneTeam iscrizione = new IscrizioneTeam(team, hackathon);
        iscrizione.aggiungiSottomissione(new Sottomissione("https://repo/l"));
        hackathon.aggiungiIscrizione(iscrizione);
        impostaStatoValutazione(hackathon);
        repositoryHackathon.saveAndFlush(hackathon);


        mockMvc.perform(post(ENDPOINT + "/{id}/valutazione", iscrizione.getSottomissione().getIdSottomissione())
                        .with(authentication(auth(GIUDICE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                               {"giudizio":"   ","punteggio":6}
                               """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("giudizio")));
    }


    @Test
    void inserisciValutazione_punteggioFuoriRange_badRequest() throws Exception {
        Hackathon hackathon = creaHackathon("HackValutazione-8");
        Team team = creaTeamConLeader("TeamM", LEADER_1);


        IscrizioneTeam iscrizione = new IscrizioneTeam(team, hackathon);
        iscrizione.aggiungiSottomissione(new Sottomissione("https://repo/m"));
        hackathon.aggiungiIscrizione(iscrizione);
        impostaStatoValutazione(hackathon);
        repositoryHackathon.saveAndFlush(hackathon);


        mockMvc.perform(post(ENDPOINT + "/{id}/valutazione", iscrizione.getSottomissione().getIdSottomissione())
                        .with(authentication(auth(GIUDICE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                               {"giudizio":"Valutazione","punteggio":11}
                               """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("punteggio")));
    }


    private Hackathon creaHackathon(String nome) {
        Hackathon hackathon = new Hackathon(
                nome,
                new Periodo(LocalDate.now().plusDays(10), LocalDate.now().plusDays(12)),
                new BigDecimal("1000"),
                "Camerino",
                5,
                3,
                LocalDateTime.now().plusDays(5),
                "Regolamento",
                20
        );


        hackathon.aggiungiStaff(new Staff(utente(GIUDICE), RuoloStaff.GIUDICE));
        return hackathon;
    }


    private void impostaStatoValutazione(Hackathon hackathon) {
        hackathon.setStato(ValutazioneInCorso.INSTANCE);
        hackathon.setStatoEnum(ValutazioneInCorso.INSTANCE);
    }


    private Team creaTeamConLeader(String nomeTeam, String nomeLeader) {
        Team team = new Team(nomeTeam);
        MembroTeam leader = new MembroTeam(utente(nomeLeader), team, RuoloTeam.LEADER);
        team.setLeader(leader);
        return repositoryTeam.saveAndFlush(team);
    }


    private UsernamePasswordAuthenticationToken auth(String nomeUtente) {
        return new UsernamePasswordAuthenticationToken(
                nomeUtente,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }


    private Utente creaUtente(String nomeUtente) {
        return new Utente(nomeUtente, nomeUtente + "@example.com", "pwd");
    }


    private Utente utente(String nomeUtente) {
        return repositoryUtente.findByNomeUtente(nomeUtente)
                .orElseThrow(() -> new AssertionError("Utente non trovato: " + nomeUtente));
    }
}