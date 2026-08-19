package unicam.cs.hackhub.testHttp;

import unicam.cs.hackhub.domain.RuoloStaff;
import unicam.cs.hackhub.domain.implementazione.Hackathon;
import unicam.cs.hackhub.domain.implementazione.Periodo;
import unicam.cs.hackhub.domain.implementazione.Staff;
import unicam.cs.hackhub.domain.implementazione.Utente;
import unicam.cs.hackhub.repository.RepositoryHackathon;
import unicam.cs.hackhub.repository.RepositoryUtente;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CreaHackathonBoundaryIT extends BaseHttpIT {


    private static final String ENDPOINT = "/api/hackathon";
    private static final String ORGANIZZATORE = "francesca";
    private static final String GIUDICE = "laura";
    private static final String MENTORE_1 = "giuseppe";
    private static final String MENTORE_2 = "mario";
    private static final String NOME_HACKATHON = "HackHub Challenge 2026";


    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private RepositoryHackathon repositoryHackathon;


    @Autowired
    private RepositoryUtente repositoryUtente;


    @MockitoSpyBean
    private ServizioNotifiche servizioNotifiche;


    @BeforeEach
    void setUp() {
        repositoryHackathon.deleteAllInBatch();
        repositoryUtente.deleteAllInBatch();


        repositoryHackathon.flush();
        repositoryUtente.flush();


        repositoryUtente.saveAndFlush(creaUtente(ORGANIZZATORE));
        repositoryUtente.saveAndFlush(creaUtente(GIUDICE));
        repositoryUtente.saveAndFlush(creaUtente(MENTORE_1));
        repositoryUtente.saveAndFlush(creaUtente(MENTORE_2));


        clearInvocations(servizioNotifiche);
    }


    @Test
    void creaHackathon_ok() throws Exception {
        String body = jsonCreaHackathon(
                GIUDICE,
                """
                ["giuseppe", "mario"]
                """
        );


        eseguiCreazione(body)
                .andExpect(status().isNoContent());


        Hackathon hackathon = repositoryHackathon.findByNome(NOME_HACKATHON)
                .orElseThrow(() -> new AssertionError("Hackathon non trovato nel database"));


        assertAll(
                () -> assertEquals(NOME_HACKATHON, hackathon.getNome()),
                () -> assertEquals("Camerino", hackathon.getLuogo()),
                () -> assertEquals(new BigDecimal("1500.00"), hackathon.getPremio()),
                () -> assertEquals(3, hackathon.getTeamMin()),
                () -> assertEquals(5, hackathon.getTeamMax()),
                () -> assertEquals(20, hackathon.getMaxIscrizioni()),
                () -> assertEquals(LocalDateTime.of(2026, 6, 10, 23, 59), hackathon.getScadenzaIscrizioni())
        );


        List<Staff> staff = hackathon.getStaff();
        assertEquals(1, staff.size(), "Alla creazione deve essere presente solo l'organizzatore");


        Staff organizzatore = staff.stream()
                .filter(s -> s.getRuolo() == RuoloStaff.ORGANIZZATORE)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Organizzatore non trovato"));


        assertEquals(ORGANIZZATORE, organizzatore.getUtente().getNomeUtente());


        Utente laura = utente(GIUDICE);
        Utente giuseppe = utente(MENTORE_1);
        Utente mario = utente(MENTORE_2);


        verify(servizioNotifiche, times(1))
                .creaInvitoStaff(ORGANIZZATORE, laura, hackathon, RuoloStaff.GIUDICE);


        verify(servizioNotifiche, times(1))
                .creaInvitoStaff(ORGANIZZATORE, giuseppe, hackathon, RuoloStaff.MENTORE);


        verify(servizioNotifiche, times(1))
                .creaInvitoStaff(ORGANIZZATORE, mario, hackathon, RuoloStaff.MENTORE);


        verifyNoMoreInteractions(servizioNotifiche);
    }


    @Test
    void creaHackathon_nomeGiaEsistente_forbidden() throws Exception {
        repositoryHackathon.saveAndFlush(creaHackathonValido());


        String body = jsonCreaHackathon(
                GIUDICE,
                """
                ["giuseppe"]
                """
        );


        eseguiCreazione(body)
                .andExpect(status().isForbidden());


        verify(servizioNotifiche, never()).creaInvitoStaff(any(), any(), any(), any());
    }


    @Test
    void creaHackathon_organizzatoreCoincideConGiudice_forbidden() throws Exception {
        String body = jsonCreaHackathon(
                ORGANIZZATORE,
                """
                ["giuseppe"]
                """
        );


        eseguiCreazione(body)
                .andExpect(status().isForbidden());


        assertFalse(repositoryHackathon.findByNome(NOME_HACKATHON).isPresent());
        verify(servizioNotifiche, never()).creaInvitoStaff(any(), any(), any(), any());
    }


    @Test
    void creaHackathon_giudicePresenteAncheTraMentori_forbidden() throws Exception {
        String body = jsonCreaHackathon(
                GIUDICE,
                """
                ["giuseppe", "laura"]
                """
        );


        eseguiCreazione(body)
                .andExpect(status().isForbidden());


        assertFalse(repositoryHackathon.findByNome(NOME_HACKATHON).isPresent());
        verify(servizioNotifiche, never()).creaInvitoStaff(any(), any(), any(), any());
    }


    @Test
    void creaHackathon_utenteStaffInesistente_notFound() throws Exception {
        String body = jsonCreaHackathon(
                "utente_inesistente",
                """
                ["giuseppe"]
                """
        );


        eseguiCreazione(body)
                .andExpect(status().isNotFound());


        assertFalse(repositoryHackathon.findByNome(NOME_HACKATHON).isPresent());
        verify(servizioNotifiche, never()).creaInvitoStaff(any(), any(), any(), any());
    }


    private ResultActions eseguiCreazione(String body) throws Exception {
        return mockMvc.perform(post(ENDPOINT)
                .with(authentication(autenticazione(ORGANIZZATORE)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }


    private UsernamePasswordAuthenticationToken autenticazione(String nomeUtente) {
        return new UsernamePasswordAuthenticationToken(
                nomeUtente,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }


    private String jsonCreaHackathon(String nomeGiudice, String nomeMentoriJsonArray) {
        return """
               {
                 "nome": "%s",
                 "dataInizio": "2026-06-20",
                 "dataFine": "2026-06-22",
                 "luogo": "Camerino",
                 "premio": 1500.00,
                 "teamMin": 3,
                 "teamMax": 5,
                 "regolamento": "Regolamento di prova",
                 "maxIscrizioni": 20,
                 "scadenzaIscrizioni": "2026-06-10T23:59:00",
                 "nomeGiudice": "%s",
                 "nomeMentori": %s
               }
               """.formatted(CreaHackathonBoundaryIT.NOME_HACKATHON, nomeGiudice, nomeMentoriJsonArray);
    }


    private Utente creaUtente(String nomeUtente) {
        return new Utente(nomeUtente, nomeUtente + "@example.com", "password123");
    }


    private Utente utente(String nomeUtente) {
        return repositoryUtente.findByNomeUtente(nomeUtente)
                .orElseThrow(() -> new AssertionError("Utente non trovato: " + nomeUtente));
    }


    private Hackathon creaHackathonValido() {
        return new Hackathon(
                CreaHackathonBoundaryIT.NOME_HACKATHON,
                new Periodo(
                        LocalDate.of(2026, 6, 20),
                        LocalDate.of(2026, 6, 22)
                ),
                new BigDecimal("1500.00"),
                "Camerino",
                5,
                3,
                LocalDateTime.of(2026, 6, 10, 23, 59),
                "Regolamento di prova",
                20
        );
    }
}
