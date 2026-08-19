package unicam.cs.hackhub.testHttp;


import unicam.cs.hackhub.domain.RuoloTeam;
import unicam.cs.hackhub.domain.implementazione.Hackathon;
import unicam.cs.hackhub.domain.implementazione.IscrizioneTeam;
import unicam.cs.hackhub.domain.implementazione.MembroTeam;
import unicam.cs.hackhub.domain.implementazione.Notifica;
import unicam.cs.hackhub.domain.implementazione.Periodo;
import unicam.cs.hackhub.domain.implementazione.Sottomissione;
import unicam.cs.hackhub.domain.implementazione.Team;
import unicam.cs.hackhub.domain.implementazione.Utente;
import unicam.cs.hackhub.domain.implementazione.statePattern.InCorso;
import unicam.cs.hackhub.domain.implementazione.statePattern.IscrizioniAperte;
import unicam.cs.hackhub.repository.RepositoryHackathon;
import unicam.cs.hackhub.repository.RepositoryIscrizioniTeam;
import unicam.cs.hackhub.repository.RepositoryMembriTeam;
import unicam.cs.hackhub.repository.RepositoryNotifica;
import unicam.cs.hackhub.repository.RepositoryTeam;
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


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GestisceSottomissioneBoundaryIT extends BaseHttpIT {


    private static final String ENDPOINT = "/api/sottomissioni";
    private static final String NOME_UTENTE = "leader";
    private static final String ALTRO_UTENTE = "membro2";
    private static final String NOME_HACKATHON = "HackFest";
    private static final String LINK_SOTTOMISSIONE = "repo-github-team-alpha";


    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private RepositoryHackathon repositoryHackathon;


    @Autowired
    private RepositoryIscrizioniTeam repositoryIscrizioniTeam;


    @Autowired
    private RepositoryMembriTeam repositoryMembriTeam;


    @Autowired
    private RepositoryNotifica repositoryNotifica;


    @Autowired
    private RepositoryTeam repositoryTeam;


    @Autowired
    private RepositoryUtente repositoryUtente;


    @Autowired
    private EntityManager entityManager;


    private Hackathon hackathon;
    private Team team;


    @BeforeEach
    void setUp() {
        repositoryNotifica.deleteAll();
        repositoryIscrizioniTeam.deleteAll();
        repositoryMembriTeam.deleteAll();
        repositoryHackathon.deleteAll();
        repositoryTeam.deleteAll();
        repositoryUtente.deleteAll();
        entityManager.flush();


        Utente leader = repositoryUtente.saveAndFlush(new Utente(NOME_UTENTE, "leader@test.it", "hash"));
        Utente membro2 = repositoryUtente.saveAndFlush(new Utente(ALTRO_UTENTE, "membro2@test.it", "hash"));


        team = repositoryTeam.saveAndFlush(new Team("TeamAlpha"));


        MembroTeam leaderMember = new MembroTeam(leader, team, RuoloTeam.LEADER);
        MembroTeam altroMember = new MembroTeam(membro2, team, RuoloTeam.MEMBRO);
        repositoryMembriTeam.saveAll(List.of(leaderMember, altroMember));
        repositoryMembriTeam.flush();


        hackathon = new Hackathon(
                NOME_HACKATHON,
                new Periodo(
                        LocalDate.now().plusDays(10),
                        LocalTime.of(9, 0),
                        LocalDate.now().plusDays(12),
                        LocalTime.of(18, 0)
                ),
                BigDecimal.valueOf(1000),
                "Camerino",
                5,
                3,
                LocalDateTime.now().plusDays(5),
                "Regolamento completo hackathon",
                20
        );
        hackathon.setStato(InCorso.INSTANCE);
        hackathon.setStatoEnum(InCorso.INSTANCE);
        hackathon = repositoryHackathon.saveAndFlush(hackathon);


        repositoryIscrizioniTeam.saveAndFlush(new IscrizioneTeam(team, hackathon));
        entityManager.flush();
        entityManager.clear();
    }


    @Test
    void inviaSottomissione_ok() throws Exception {
        mockMvc.perform(post(ENDPOINT + "/{nomeHackathon}", hackathon.getNome())
                        .with(authentication(auth()))
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(LINK_SOTTOMISSIONE))
                .andExpect(status().isOk())
                .andExpect(content().string(""));


        entityManager.flush();
        entityManager.clear();


        IscrizioneTeam iscrizione = repositoryIscrizioniTeam.findByTeam(team).orElseThrow();
        assertNotNull(iscrizione.getSottomissione());
        assertEquals(LINK_SOTTOMISSIONE, iscrizione.getSottomissione().getLink());


        List<Notifica> notifiche = repositoryNotifica.findAll();
        assertEquals(1, notifiche.size());
        assertTrue(notifiche.get(0).getPayload().contains(NOME_UTENTE + " ha modificato la sottomissione dell'hackathon " + NOME_HACKATHON));
    }


    @Test
    void inviaSottomissione_notFound() throws Exception {
        mockMvc.perform(post(ENDPOINT + "/{nomeHackathon}", NOME_HACKATHON)
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(
                                        "utenteSenzaTeam",
                                        null,
                                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                                )
                        ))
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(LINK_SOTTOMISSIONE))
                .andExpect(status().isNotFound());
    }


    @Test
    void inviaSottomissione_errore() throws Exception {
        hackathon.setStato(IscrizioniAperte.INSTANCE);
        hackathon.setStatoEnum(IscrizioniAperte.INSTANCE);
        repositoryHackathon.saveAndFlush(hackathon);
        entityManager.flush();
        entityManager.clear();


        mockMvc.perform(post(ENDPOINT + "/{nomeHackathon}", NOME_HACKATHON)
                        .with(authentication(auth()))
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(LINK_SOTTOMISSIONE))
                .andExpect(status().isConflict());
    }


    @Test
    void attivaRimozioneSottomissione_ok() throws Exception {
        IscrizioneTeam iscrizione = repositoryIscrizioniTeam.findByTeam(team).orElseThrow();
        iscrizione.aggiungiSottomissione(new Sottomissione(LINK_SOTTOMISSIONE));
        repositoryIscrizioniTeam.saveAndFlush(iscrizione);
        entityManager.flush();
        entityManager.clear();


        mockMvc.perform(delete(ENDPOINT + "/{nomeHackathon}", NOME_HACKATHON)
                        .with(authentication(auth())))
                .andExpect(status().isOk())
                .andExpect(content().string(""));


        entityManager.flush();
        entityManager.clear();


        IscrizioneTeam aggiornata = repositoryIscrizioniTeam.findByTeam(team).orElseThrow();
        assertNull(aggiornata.getSottomissione());


        List<Notifica> notifiche = repositoryNotifica.findAll();
        assertEquals(1, notifiche.size());
        assertTrue(notifiche.get(0).getPayload().contains(NOME_UTENTE + " ha attivato la rimozione della sottomissione dell'hackathon " + NOME_HACKATHON));
    }


    @Test
    void attivaRimozioneSottomissione_notFound() throws Exception {
        mockMvc.perform(delete(ENDPOINT + "/{nomeHackathon}", NOME_HACKATHON)
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(
                                        "utenteSenzaTeam",
                                        null,
                                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                                )
                        )))
                .andExpect(status().isNotFound());
    }


    @Test
    void attivaRimozioneSottomissione_errore() throws Exception {
        IscrizioneTeam iscrizione = repositoryIscrizioniTeam.findByTeam(team).orElseThrow();
        iscrizione.aggiungiSottomissione(new Sottomissione(LINK_SOTTOMISSIONE));
        repositoryIscrizioniTeam.saveAndFlush(iscrizione);


        hackathon.setStato(IscrizioniAperte.INSTANCE);
        hackathon.setStatoEnum(IscrizioniAperte.INSTANCE);
        repositoryHackathon.saveAndFlush(hackathon);
        entityManager.flush();
        entityManager.clear();


        mockMvc.perform(delete(ENDPOINT + "/{nomeHackathon}", NOME_HACKATHON)
                        .with(authentication(auth())))
                .andExpect(status().isConflict());
    }


    private UsernamePasswordAuthenticationToken auth() {
        return new UsernamePasswordAuthenticationToken(
                GestisceSottomissioneBoundaryIT.NOME_UTENTE,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}

