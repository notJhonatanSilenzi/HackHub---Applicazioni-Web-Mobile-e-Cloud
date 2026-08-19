package unicam.cs.hackhub.testHttp;

import unicam.cs.hackhub.domain.RuoloStaff;
import unicam.cs.hackhub.domain.TipoNotifica;
import unicam.cs.hackhub.domain.implementazione.*;
import unicam.cs.hackhub.domain.implementazione.*;
import unicam.cs.hackhub.repository.RepositoryHackathon;
import unicam.cs.hackhub.repository.RepositoryNotifica;
import unicam.cs.hackhub.repository.RepositoryRichiesta;
import unicam.cs.hackhub.repository.RepositoryStaff;
import unicam.cs.hackhub.repository.RepositoryUtente;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;




import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;




import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;




@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class VisualizzaBoundaryIT extends BaseHttpIT {




    private static final String BASE_URL = "/api";
    private static final String NOME_UTENTE = "francesca";




    @Autowired
    private MockMvc mockMvc;




    @Autowired
    private RepositoryHackathon repositoryHackathon;




    @Autowired
    private RepositoryRichiesta repositoryRichiesta;




    @Autowired
    private RepositoryNotifica repositoryNotifica;




    @Autowired
    private RepositoryUtente repositoryUtente;




    @Autowired
    private RepositoryStaff repositoryStaff;




    @Autowired
    private EntityManager entityManager;




    private Utente utente;
    private Hackathon hackathon;




    @BeforeEach
    void setUp() {
        repositoryRichiesta.deleteAll();
        repositoryNotifica.deleteAll();
        repositoryStaff.deleteAll();
        repositoryHackathon.deleteAll();
        repositoryUtente.deleteAll();




        utente = new Utente(NOME_UTENTE, "francesca@example.it", "hash123");
        repositoryUtente.saveAndFlush(utente);




        Periodo periodoHackathon = new Periodo(
                LocalDate.now().plusDays(5),
                LocalTime.of(9, 0),
                LocalDate.now().plusDays(7),
                LocalTime.of(18, 0)
        );




        hackathon = new Hackathon(
                "HackathonTest",
                periodoHackathon,
                BigDecimal.valueOf(1000),
                "Camerino",
                5,
                3,
                LocalDateTime.now().plusDays(2),
                "Regolamento di test",
                10
        );
        repositoryHackathon.saveAndFlush(hackathon);




        Staff staff = new Staff(utente, RuoloStaff.GIUDICE);
        hackathon.aggiungiStaff(staff);
        repositoryHackathon.saveAndFlush(hackathon);
    }




    @Test
    void viewValutazioni_ok() throws Exception {
        Team team1 = new Team("teamx");
        Team team2 = new Team("teamy");
        entityManager.persist(team1);
        entityManager.persist(team2);




        Valutazione valutazione1 = new Valutazione(8, "Buono");
        Valutazione valutazione2 = new Valutazione(9, "Ottimo");
        entityManager.persist(valutazione1);
        entityManager.persist(valutazione2);




        Sottomissione sottomissione1 = new Sottomissione("link-sub-1");
        sottomissione1.impostaValutazione(valutazione1);




        Sottomissione sottomissione2 = new Sottomissione("link-sub-2");
        sottomissione2.impostaValutazione(valutazione2);




        IscrizioneTeam iscrizione1 = new IscrizioneTeam(team1, hackathon);
        iscrizione1.aggiungiSottomissione(sottomissione1);




        IscrizioneTeam iscrizione2 = new IscrizioneTeam(team2, hackathon);
        iscrizione2.aggiungiSottomissione(sottomissione2);




        hackathon.aggiungiIscrizione(iscrizione1);
        hackathon.aggiungiIscrizione(iscrizione2);
        repositoryHackathon.saveAndFlush(hackathon);




        mockMvc.perform(get(BASE_URL + "/hackathon/{nomeHackathon}/valutazioni", hackathon.getNome())
                        .with(authentication(auth())))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].giudizio", containsInAnyOrder("Buono", "Ottimo")))
                .andExpect(jsonPath("$[*].punteggio", containsInAnyOrder(8, 9)));
    }




    @Test
    void viewSottomissioni_ok() throws Exception {
        Team team1 = new Team("teamx");
        Team team2 = new Team("teamy");
        entityManager.persist(team1);
        entityManager.persist(team2);




        Valutazione valutazione1 = new Valutazione(8, "Buono");
        Valutazione valutazione2 = new Valutazione(9, "Ottimo");
        entityManager.persist(valutazione1);
        entityManager.persist(valutazione2);




        Sottomissione sottomissione1 = new Sottomissione("link-sub-1");
        sottomissione1.impostaValutazione(valutazione1);




        Sottomissione sottomissione2 = new Sottomissione("link-sub-2");
        sottomissione2.impostaValutazione(valutazione2);




        IscrizioneTeam iscrizione1 = new IscrizioneTeam(team1, hackathon);
        iscrizione1.aggiungiSottomissione(sottomissione1);




        IscrizioneTeam iscrizione2 = new IscrizioneTeam(team2, hackathon);
        iscrizione2.aggiungiSottomissione(sottomissione2);




        hackathon.aggiungiIscrizione(iscrizione1);
        hackathon.aggiungiIscrizione(iscrizione2);
        repositoryHackathon.saveAndFlush(hackathon);




        mockMvc.perform(get(BASE_URL + "/hackathon/{nomeHackathon}/sottomissioni", hackathon.getNome())
                        .with(authentication(auth())))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].link", containsInAnyOrder("link-sub-1", "link-sub-2")))
                .andExpect(jsonPath("$[*].giudizio", containsInAnyOrder("Buono", "Ottimo")))
                .andExpect(jsonPath("$[*].punteggio", containsInAnyOrder(8, 9)));
    }




    @Test
    void viewIscrizioni_ok() throws Exception {
        Team team1 = new Team("teamx");
        Team team2 = new Team("teamy");
        entityManager.persist(team1);
        entityManager.persist(team2);




        Sottomissione sottomissione1 = new Sottomissione("link-sub-1");
        Sottomissione sottomissione2 = new Sottomissione("link-sub-2");




        IscrizioneTeam iscrizione1 = new IscrizioneTeam(team1, hackathon);
        iscrizione1.aggiungiSottomissione(sottomissione1);




        IscrizioneTeam iscrizione2 = new IscrizioneTeam(team2, hackathon);
        iscrizione2.aggiungiSottomissione(sottomissione2);




        hackathon.aggiungiIscrizione(iscrizione1);
        hackathon.aggiungiIscrizione(iscrizione2);
        repositoryHackathon.saveAndFlush(hackathon);




        mockMvc.perform(get(BASE_URL + "/hackathon/{nomeHackathon}/iscrizioni", hackathon.getNome())
                        .with(authentication(auth())))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].nomeHackathon", containsInAnyOrder("HackathonTest", "HackathonTest")))
                .andExpect(jsonPath("$[*].nomeTeam", containsInAnyOrder("teamx", "teamy")))
                .andExpect(jsonPath("$[*].linkSottomissione", containsInAnyOrder("link-sub-1", "link-sub-2")));
    }




    @Test
    void viewRichieste_ok() throws Exception {
        PropostaCall richiesta1 = new PropostaCall(
                "mentor1",
                "payload-1",
                utente,
                LocalDateTime.now().plusDays(1),
                new Periodo(
                        LocalDate.now().plusDays(1),
                        LocalTime.of(10, 0),
                        LocalDate.now().plusDays(1),
                        LocalTime.of(11, 0)
                )
        );




        PropostaCall richiesta2 = new PropostaCall(
                "mentor2",
                "payload-2",
                utente,
                LocalDateTime.now().plusDays(1),
                new Periodo(
                        LocalDate.now().plusDays(2),
                        LocalTime.of(15, 0),
                        LocalDate.now().plusDays(2),
                        LocalTime.of(16, 0)
                )
        );




        repositoryRichiesta.saveAll(List.of(richiesta1, richiesta2));
        repositoryRichiesta.flush();




        mockMvc.perform(get(BASE_URL + "/richieste")
                        .with(authentication(auth())))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].payload", containsInAnyOrder("payload-1", "payload-2")));
    }




    @Test
    void viewNotifiche_ok() throws Exception {
        Notifica notifica1 = new Notifica("notifica-1", utente, TipoNotifica.AVVIO_HACKATHON);
        Notifica notifica2 = new Notifica("notifica-2", utente, TipoNotifica.VALUTAZIONE_CONCLUSA);




        repositoryNotifica.saveAll(List.of(notifica1, notifica2));
        repositoryNotifica.flush();




        mockMvc.perform(get(BASE_URL + "/notifiche")
                        .with(authentication(auth())))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].messaggio", containsInAnyOrder("notifica-1", "notifica-2")));
    }


    @Test
    void viewInfoHackathon_pubblica_ok() throws Exception {
        mockMvc.perform(get(BASE_URL + "/hackathon"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("HackathonTest"));
    }




    private UsernamePasswordAuthenticationToken auth() {
        return new UsernamePasswordAuthenticationToken(
                VisualizzaBoundaryIT.NOME_UTENTE,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}


