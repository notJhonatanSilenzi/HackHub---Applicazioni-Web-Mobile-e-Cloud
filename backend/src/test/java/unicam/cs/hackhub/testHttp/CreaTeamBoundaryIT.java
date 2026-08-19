package unicam.cs.hackhub.testHttp;

import unicam.cs.hackhub.domain.RuoloTeam;
import unicam.cs.hackhub.domain.implementazione.MembroTeam;
import unicam.cs.hackhub.domain.implementazione.Team;
import unicam.cs.hackhub.domain.implementazione.Utente;
import unicam.cs.hackhub.repository.RepositoryMembriTeam;
import unicam.cs.hackhub.repository.RepositoryTeam;
import unicam.cs.hackhub.repository.RepositoryUtente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CreaTeamBoundaryIT extends BaseHttpIT {


    private static final String ENDPOINT = "/api/team";
    private static final String UTENTE_1 = "francesca";
    private static final String UTENTE_2 = "laura";
    private static final String NOME_TEAM = "CodeQueens";


    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private RepositoryTeam repositoryTeam;


    @Autowired
    private RepositoryMembriTeam repositoryMembriTeam;


    @Autowired
    private RepositoryUtente repositoryUtente;


    @BeforeEach
    void setUp() {
        repositoryMembriTeam.deleteAllInBatch();
        repositoryTeam.deleteAllInBatch();
        repositoryUtente.deleteAllInBatch();


        repositoryMembriTeam.flush();
        repositoryTeam.flush();
        repositoryUtente.flush();


        repositoryUtente.saveAndFlush(creaUtente(UTENTE_1));
        repositoryUtente.saveAndFlush(creaUtente(UTENTE_2));
    }


    @Test
    void creaTeam_ok() throws Exception {
        eseguiCreazione(UTENTE_1, NOME_TEAM)
                .andExpect(status().isNoContent());


        Team team = repositoryTeam.findByNome(NOME_TEAM)
                .orElseThrow(() -> new AssertionError("Team non trovato nel database"));


        assertEquals(NOME_TEAM, team.getNome());


        MembroTeam leader = team.getMembri().stream().filter(membroTeam -> membroTeam.getRuolo().equals(RuoloTeam.LEADER)).findFirst()
                .orElseThrow(() -> new AssertionError("Leader non trovato tra i membri del team"));


        assertAll(
                () -> assertEquals(UTENTE_1, leader.getUtente().getNomeUtente()),
                () -> assertEquals(RuoloTeam.LEADER, leader.getRuolo())
        );


        assertTrue(
                repositoryMembriTeam.existsByUtente(utente(UTENTE_1)),
                "L'utente creatore dovrebbe risultare membro di un team"
        );
    }


    @Test
    void creaTeam_utenteGiaMembro_forbidden() throws Exception {
        Utente utente = utente(UTENTE_1);
        Team team = new Team("TeamEsistente");
        MembroTeam leader = new MembroTeam(utente, team, RuoloTeam.LEADER);
        team.setLeader(leader);
        repositoryTeam.saveAndFlush(team);


        eseguiCreazione(UTENTE_1, NOME_TEAM)
                .andExpect(status().isForbidden());


        assertFalse(repositoryTeam.findByNome(NOME_TEAM).isPresent());
    }


    @Test
    void creaTeam_nomeGiaEsistente_conflict() throws Exception {
        Team team = new Team(NOME_TEAM);
        repositoryTeam.saveAndFlush(team);


        eseguiCreazione(UTENTE_1, NOME_TEAM)
                .andExpect(status().isConflict());
    }


    @Test
    void creaTeam_utenteAutenticatoInesistente_notFound() throws Exception {
        eseguiCreazione("utente_inesistente", NOME_TEAM)
                .andExpect(status().isNotFound());


        assertFalse(repositoryTeam.findByNome(NOME_TEAM).isPresent());
    }


    private ResultActions eseguiCreazione(String nomeUtente, String nomeTeam) throws Exception {
        return mockMvc.perform(post(ENDPOINT)
                .with(authentication(autenticazione(nomeUtente)))
                .contentType(MediaType.TEXT_PLAIN)
                .content(nomeTeam));
    }


    private UsernamePasswordAuthenticationToken autenticazione(String nomeUtente) {
        return new UsernamePasswordAuthenticationToken(
                nomeUtente,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }


    private String jsonString(String valore) {
        return "\"" + valore + "\"";
    }


    private Utente creaUtente(String nomeUtente) {
        return new Utente(nomeUtente, nomeUtente + "@example.com", "password123");
    }


    private Utente utente(String nomeUtente) {
        return repositoryUtente.findByNomeUtente(nomeUtente)
                .orElseThrow(() -> new AssertionError("Utente non trovato: " + nomeUtente));
    }
}
