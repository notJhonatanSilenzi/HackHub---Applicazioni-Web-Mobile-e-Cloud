package unicam.cs.hackhub.testHttp;


import unicam.cs.hackhub.domain.TipoNotifica;
import unicam.cs.hackhub.domain.implementazione.*;
import unicam.cs.hackhub.domain.RuoloStaff;
import unicam.cs.hackhub.domain.implementazione.*;
import unicam.cs.hackhub.repository.RepositoryHackathon;
import unicam.cs.hackhub.repository.RepositoryNotifica;
import unicam.cs.hackhub.repository.RepositoryStaff;
import unicam.cs.hackhub.repository.RepositoryUtente;
import unicam.cs.hackhub.servizi.ServizioNotifiche;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RispondeRichiesteSupportoBoundaryIT extends BaseHttpIT {


    private static final String ENDPOINT = "/api/richieste-supporto/risposta";
    private static final String MENTORE = "mentore_user";
    private static final String DEST = "dest_user";


    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private RepositoryNotifica repositoryNotifica;


    @Autowired
    private RepositoryStaff repositoryStaff;


    @Autowired
    private RepositoryUtente repositoryUtente;


    @Autowired
    private RepositoryHackathon repositoryHackathon;


    @MockitoSpyBean
    private ServizioNotifiche servizioNotifiche;


    @BeforeEach
    void setUp(){
        repositoryNotifica.deleteAllInBatch();
        repositoryStaff.deleteAllInBatch();
        repositoryUtente.deleteAllInBatch();
        repositoryHackathon.deleteAllInBatch();


        repositoryNotifica.flush();
        repositoryStaff.flush();
        repositoryUtente.flush();
        repositoryHackathon.flush();


        repositoryUtente.saveAndFlush(new Utente(MENTORE, MENTORE + "@example.com", "pwd"));
        repositoryUtente.saveAndFlush(new Utente(DEST, DEST + "@example.com", "pwd"));


        clearInvocations(servizioNotifiche);
    }


    @Test
    void rispondiRichiestaSupporto_ok() throws Exception {
        Utente destinatario = repositoryUtente.findByNomeUtente(DEST).orElseThrow();


        Notifica not = new Notifica("help", destinatario, TipoNotifica.RICHIESTA_SUPPORTO);
        repositoryNotifica.saveAndFlush(not);


        Staff staff = new Staff(repositoryUtente.findByNomeUtente(MENTORE).orElseThrow(), RuoloStaff.MENTORE);
        Hackathon hackathon = new Hackathon("HackathonTest", new Periodo(
                LocalDate.of(2026, 6, 20),
                LocalDate.of(2026, 6, 22)
        ),
                new BigDecimal("1500.00"),
                "Camerino",
                5,
                3,
                LocalDateTime.of(2026, 6, 10, 23, 59),
                "Regolamento di prova",
                20);
        staff.setHackathon(hackathon);
        repositoryHackathon.saveAndFlush(hackathon);
        repositoryStaff.saveAndFlush(staff);


        mockMvc.perform(post(ENDPOINT)
                        .with(authentication(new UsernamePasswordAuthenticationToken(MENTORE, null, List.of(new SimpleGrantedAuthority("ROLE_USER")))))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("idNotifica", not.getIdNotifica()))
                .andExpect(status().isOk());


        verify(servizioNotifiche, times(1)).creaNotifica(eq(destinatario), eq(TipoNotifica.RICHIESTA_SUPPORTO), any());
    }


    @Test
    void rispondiRichiestaSupporto_notFound_notFound() throws Exception {
        // staff esiste
        Hackathon hackathon = new Hackathon("HackathonTest", new Periodo(
                LocalDate.of(2026, 6, 20),
                LocalDate.of(2026, 6, 22)
        ),
                new BigDecimal("1500.00"),
                "Camerino",
                5,
                3,
                LocalDateTime.of(2026, 6, 10, 23, 59),
                "Regolamento di prova",
                20);
        repositoryHackathon.saveAndFlush(hackathon);
        Staff staff = new Staff(repositoryUtente.findByNomeUtente(MENTORE).orElseThrow(), RuoloStaff.MENTORE);
        staff.setHackathon(hackathon);
        repositoryStaff.saveAndFlush(staff);


        mockMvc.perform(post(ENDPOINT)
                        .with(authentication(new UsernamePasswordAuthenticationToken(MENTORE, null, List.of(new SimpleGrantedAuthority("ROLE_USER")))))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("idNotifica", "no-id"))
                .andExpect(status().isNotFound());
    }


    @Test
    void rispondiRichiestaSupporto_staffNotFound_notFound() throws Exception {
        Utente destinatario = repositoryUtente.findByNomeUtente(DEST).orElseThrow();
        Notifica not = new Notifica("help", destinatario, TipoNotifica.RICHIESTA_SUPPORTO);
        repositoryNotifica.saveAndFlush(not);


        mockMvc.perform(post(ENDPOINT)
                        .with(authentication(new UsernamePasswordAuthenticationToken(MENTORE, null, List.of(new SimpleGrantedAuthority("ROLE_USER")))))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("idNotifica", not.getIdNotifica()))
                .andExpect(status().isNotFound());
    }
}
