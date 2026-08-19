package unicam.cs.hackhub.testHttp;


import unicam.cs.hackhub.domain.RuoloStaff;
import unicam.cs.hackhub.domain.RuoloTeam;
import unicam.cs.hackhub.domain.TipoNotifica;
import unicam.cs.hackhub.domain.implementazione.*;
import unicam.cs.hackhub.repository.*;
import unicam.cs.hackhub.domain.StatoRichiesta;
import unicam.cs.hackhub.domain.implementazione.*;
import unicam.cs.hackhub.repository.*;
import unicam.cs.hackhub.servizi.ServizioNotifiche;
import unicam.cs.hackhub.servizi.esterni.CalendarioMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDateTime;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class GestisciRichiesteBoundaryIT extends BaseHttpIT {


    private static final String ENDPOINT = "/api/richieste";
    private static final String DESTinatARIO = "dest";
    private static final String ALTRO = "altro";
    private static final String MITTENTE = "mittente";
    private static final String ORGANIZZATORE = "org";


    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private RepositoryUtente repositoryUtente;


    @Autowired
    private RepositoryRichiesta repositoryRichiesta;


    @Autowired
    private RepositoryHackathon repositoryHackathon;


    @Autowired
    private RepositoryStaff repositoryStaff;


    @Autowired
    private RepositoryMembriTeam repositoryMembriTeam;


    @Autowired
    private RepositoryTeam repositoryTeam;


    @Autowired
    private RepositoryNotifica repositoryNotifica;


    @MockitoSpyBean
    private ServizioNotifiche servizioNotifiche;


    @MockitoSpyBean
    private CalendarioMock calendario;


    private Utente destinatario;


    @BeforeEach
    void setUp() {
        repositoryNotifica.deleteAllInBatch();
        repositoryRichiesta.deleteAllInBatch();
        repositoryStaff.deleteAllInBatch();
        repositoryMembriTeam.deleteAllInBatch();
        repositoryTeam.deleteAllInBatch();
        repositoryHackathon.deleteAllInBatch();
        repositoryUtente.deleteAllInBatch();


        repositoryNotifica.flush();
        repositoryRichiesta.flush();
        repositoryStaff.flush();
        repositoryMembriTeam.flush();
        repositoryTeam.flush();
        repositoryHackathon.flush();
        repositoryUtente.flush();


        repositoryUtente.saveAndFlush(creaUtente(DESTinatARIO));
        repositoryUtente.saveAndFlush(creaUtente(ALTRO));
        repositoryUtente.saveAndFlush(creaUtente(MITTENTE));
        repositoryUtente.saveAndFlush(creaUtente(ORGANIZZATORE));


        destinatario = utente(DESTinatARIO);


        clearInvocations(servizioNotifiche);
        clearInvocations(calendario);
    }


    @Test
    void accettaInvitoStaff_ok() throws Exception {
        Hackathon hackathon = creaHackathonValido();
        // organizzatote
        Utente org = utente(ORGANIZZATORE);
        hackathon.aggiungiStaff(new Staff(org, RuoloStaff.ORGANIZZATORE));
        repositoryHackathon.saveAndFlush(hackathon);


        InvitoStaff inv = new InvitoStaff(MITTENTE, "msg", destinatario, LocalDateTime.now().plusDays(1), hackathon, RuoloStaff.GIUDICE);
        repositoryRichiesta.saveAndFlush(inv);


        mockMvc.perform(post(ENDPOINT + "/%s/accetta".formatted(inv.getIdRichiesta()))
                        .with(authentication(auth())))
                .andExpect(status().isAccepted());


        Hackathon h = repositoryHackathon.findByIdFetchStaff(hackathon.getIdHackathon()).orElseGet(() -> repositoryHackathon.findById(hackathon.getIdHackathon()).orElseThrow());
        assertTrue(h.getStaff().stream().anyMatch(s -> s.getRuolo() == RuoloStaff.GIUDICE));


        List<Notifica> not = repositoryNotifica.findAll();
        assertEquals(1, not.size());
        verify(servizioNotifiche, times(1)).creaNotifica(any(), eq(TipoNotifica.ACCETTA_RICHIESTA), any());
    }


    @Test
    void accettaInvitoTeam_ok() throws Exception {
        Team team = new Team("TeamX");
        repositoryTeam.saveAndFlush(team);


        Utente leaderUser = utente(ORGANIZZATORE);
        MembroTeam leader = new MembroTeam(leaderUser, team, RuoloTeam.LEADER);
        repositoryMembriTeam.saveAndFlush(leader);


        InvitoTeam inv = new InvitoTeam(MITTENTE, "msg", destinatario, LocalDateTime.now().plusDays(1), repositoryTeam.findById(team.getIdTeam()).orElseThrow());
        repositoryRichiesta.saveAndFlush(inv);


        assertTrue(repositoryUtente.findByNomeUtente(DESTinatARIO).isPresent(), "destinatario must exist before request");


        mockMvc.perform(post(ENDPOINT + "/%s/accetta".formatted(inv.getIdRichiesta()))
                        .with(authentication(auth())))
                .andExpect(status().isAccepted());


        Richiesta saved = repositoryRichiesta.findById(inv.getIdRichiesta()).orElseThrow();
        assertEquals(StatoRichiesta.ACCETTATO, saved.getStato());
        verify(servizioNotifiche, times(1)).creaNotifica(any(), eq(TipoNotifica.ACCETTA_RICHIESTA), any());
    }


    @Test
    void accettaPropostaCall_ok() throws Exception {
        Team team = new Team("CallTeam");
        repositoryTeam.saveAndFlush(team);
        MembroTeam membro = new MembroTeam(destinatario, team, RuoloTeam.MEMBRO);
        repositoryMembriTeam.saveAndFlush(membro);


        assertTrue(repositoryUtente.findByNomeUtente(MITTENTE).isPresent(), "mentore (mittente) must exist");
        Utente mentore = repositoryUtente.findByNomeUtente(MITTENTE).orElseThrow();
        Staff s = new Staff(mentore, RuoloStaff.MENTORE);
        Hackathon h = creaHackathonValido();
        h.aggiungiStaff(s);
        repositoryHackathon.saveAndFlush(h);


        Utente destinatarioManaged = repositoryUtente.findByNomeUtente(DESTinatARIO).orElseThrow();


        PropostaCall pc = new PropostaCall(MITTENTE, "payload", destinatarioManaged, LocalDateTime.now().plusDays(1), new Periodo(
                java.time.LocalDate.now().plusDays(1), java.time.LocalTime.of(10,0),
                java.time.LocalDate.now().plusDays(1), java.time.LocalTime.of(11,0)
        ));
        repositoryRichiesta.saveAndFlush(pc);


        mockMvc.perform(post(ENDPOINT + "/%s/accetta".formatted(pc.getIdRichiesta()))
                        .with(authentication(auth())))
                .andExpect(status().isAccepted());


        verify(calendario, times(1)).salvaCall(any());
        verify(servizioNotifiche, times(1)).creaNotifica(any(), eq(TipoNotifica.ACCETTA_RICHIESTA), any());
    }


    @Test
    void rifiutaRichiesta_ok() throws Exception {
        Team t = new Team("TReject");
        repositoryTeam.saveAndFlush(t);
        Team tManaged = repositoryTeam.findById(t.getIdTeam()).orElseThrow();
        InvitoTeam inv = new InvitoTeam(MITTENTE, "msg", destinatario, LocalDateTime.now().plusDays(1), tManaged);
        repositoryRichiesta.saveAndFlush(inv);


        mockMvc.perform(post(ENDPOINT + "/%s/rifiuta".formatted(inv.getIdRichiesta()))
                        .with(authentication(auth())))
                .andExpect(status().isOk());


        Richiesta r = repositoryRichiesta.findById(inv.getIdRichiesta()).orElseThrow();
        assertEquals(StatoRichiesta.RIFIUTATO, r.getStato());
        verify(servizioNotifiche, times(1)).creaNotifica(any(), eq(TipoNotifica.RIFIUTO_RICHIESTA), any());
    }


    @Test
    void accettaRichiesta_userNotFound_notFound() throws Exception {
        Team t = new Team("T2");
        repositoryTeam.saveAndFlush(t);
        Team tManaged = repositoryTeam.findById(t.getIdTeam()).orElseThrow();
        InvitoTeam inv = new InvitoTeam(MITTENTE, "msg", destinatario, LocalDateTime.now().plusDays(1), tManaged);
        repositoryRichiesta.saveAndFlush(inv);


        mockMvc.perform(post(ENDPOINT + "/%s/accetta".formatted(inv.getIdRichiesta()))
                        .with(authentication(new UsernamePasswordAuthenticationToken("utenteInesistente", null, List.of(new SimpleGrantedAuthority("ROLE_USER"))))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Utente non trovato"));
    }


    @Test
    void accettaRichiesta_destinatarioDiverso_forbidden() throws Exception {
        Team t = new Team("T3");
        repositoryTeam.saveAndFlush(t);
        InvitoTeam inv = new InvitoTeam(MITTENTE, "msg", destinatario, LocalDateTime.now().plusDays(1), t);
        repositoryRichiesta.saveAndFlush(inv);


        mockMvc.perform(post(ENDPOINT + "/%s/accetta".formatted(inv.getIdRichiesta()))
                        .with(authentication(new UsernamePasswordAuthenticationToken(ALTRO, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("L'utente autenticato non è il destinatario della richiesta"));
    }


    @Test
    void rifiutaRichiesta_giaElaborata_conflict() throws Exception {
        Team t = new Team("T4");
        repositoryTeam.saveAndFlush(t);
        InvitoTeam inv = new InvitoTeam(MITTENTE, "msg", destinatario, LocalDateTime.now().plusDays(1), t);
        inv.accetta();
        repositoryRichiesta.saveAndFlush(inv);


        mockMvc.perform(post(ENDPOINT + "/%s/rifiuta".formatted(inv.getIdRichiesta()))
                        .with(authentication(auth())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("La richiesta è già stata elaborata"));
    }


    @Test
    void accettaRichiesta_scaduta_conflict() throws Exception {
        Team t = new Team("T5");
        repositoryTeam.saveAndFlush(t);
        InvitoTeam inv = new InvitoTeam(MITTENTE, "msg", destinatario, LocalDateTime.now().minusMinutes(1), t);
        repositoryRichiesta.saveAndFlush(inv);


        mockMvc.perform(post(ENDPOINT + "/%s/accetta".formatted(inv.getIdRichiesta()))
                        .with(authentication(auth())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("La richiesta è scaduta"));
    }


    private UsernamePasswordAuthenticationToken auth() {
        return new UsernamePasswordAuthenticationToken(
                GestisciRichiesteBoundaryIT.DESTinatARIO,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }


    // helper methods
    private Utente creaUtente(String nomeUtente) {
        return new Utente(nomeUtente, nomeUtente + "@example.com", "pwd");
    }


    private Utente utente(String nomeUtente) {
        return repositoryUtente.findByNomeUtente(nomeUtente)
                .orElseThrow(() -> new AssertionError("Utente non trovato: " + nomeUtente));
    }


    private Hackathon creaHackathonValido() {
        return repositoryHackathon.saveAndFlush(new Hackathon(
                "HackTest-" + System.nanoTime(),
                new Periodo(java.time.LocalDate.now().plusDays(1), java.time.LocalTime.of(9,0), java.time.LocalDate.now().plusDays(2), java.time.LocalTime.of(18,0)),
                java.math.BigDecimal.valueOf(1000),
                "Loc",
                5,
                3,
                java.time.LocalDateTime.now().plusDays(1),
                "reg",
                10
        ));
    }
}
