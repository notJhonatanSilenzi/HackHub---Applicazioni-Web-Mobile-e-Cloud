package unicam.cs.hackhub.testHttp;


import unicam.cs.hackhub.domain.RuoloTeam;
import unicam.cs.hackhub.domain.implementazione.Hackathon;
import unicam.cs.hackhub.domain.implementazione.IscrizioneTeam;
import unicam.cs.hackhub.domain.implementazione.MembroTeam;
import unicam.cs.hackhub.domain.implementazione.Periodo;
import unicam.cs.hackhub.domain.implementazione.statePattern.InCorso;
import unicam.cs.hackhub.domain.implementazione.Team;
import unicam.cs.hackhub.domain.implementazione.Utente;
import unicam.cs.hackhub.repository.RepositoryHackathon;
import unicam.cs.hackhub.repository.RepositoryIscrizioniTeam;
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
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class IscriviTeamBoundaryIT extends BaseHttpIT {


    private static final String ENDPOINT = "/api/hackathon";
    private static final String LEADER = "leader_user";
    private static final String MEMBER = "member_user";
    private static final String HACK_NAME = "HackTest";


    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private RepositoryUtente repositoryUtente;


    @Autowired
    private RepositoryTeam repositoryTeam;


    @Autowired
    private RepositoryMembriTeam repositoryMembriTeam;


    @Autowired
    private RepositoryHackathon repositoryHackathon;


    @Autowired
    private RepositoryIscrizioniTeam repositoryIscrizioniTeam;


    @BeforeEach
    void setUp(){
        repositoryIscrizioniTeam.deleteAllInBatch();
        repositoryMembriTeam.deleteAllInBatch();
        repositoryTeam.deleteAllInBatch();
        repositoryHackathon.deleteAllInBatch();
        repositoryUtente.deleteAllInBatch();


        repositoryIscrizioniTeam.flush();
        repositoryMembriTeam.flush();
        repositoryTeam.flush();
        repositoryHackathon.flush();
        repositoryUtente.flush();


        repositoryUtente.saveAndFlush(creaUtente(LEADER));
        repositoryUtente.saveAndFlush(creaUtente(MEMBER));
    }


    @Test
    void avviaIscrizioneHackathon_ok() throws Exception {
        Team team = new Team("TeamA");
        MembroTeam leader = new MembroTeam(utente(LEADER), team, RuoloTeam.LEADER);
        team.setLeader(leader);
        repositoryUtente.saveAndFlush(creaUtente("m2"));
        repositoryUtente.saveAndFlush(creaUtente("m3"));
        MembroTeam membro2 = new MembroTeam(utente("m2"), team, RuoloTeam.MEMBRO);
        MembroTeam membro3 = new MembroTeam(utente("m3"), team, RuoloTeam.MEMBRO);
        team.aggiungiMembro(membro2);
        team.aggiungiMembro(membro3);
        repositoryTeam.saveAndFlush(team);


        Hackathon hackathon = creaHackathonValido();
        repositoryHackathon.saveAndFlush(hackathon);


        mockMvc.perform(post(ENDPOINT + "/%s/iscrizioni".formatted(hackathon.getNome()))
                        .with(authentication(autenticazione(LEADER)))
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isNoContent());


        Hackathon persisted = repositoryHackathon.findByNome(hackathon.getNome()).orElseThrow();
        assertTrue(persisted.getIscrizioni().stream().anyMatch(i -> i.getTeam().getNome().equals(team.getNome())));
    }


    @Test
    void avviaIscrizioneHackathon_notMember_notFound() throws Exception {
        Hackathon hackathon = creaHackathonValido();
        repositoryHackathon.saveAndFlush(hackathon);


        mockMvc.perform(post(ENDPOINT + "/%s/iscrizioni".formatted(hackathon.getNome()))
                        .with(authentication(autenticazione("non_membro"))))
                .andExpect(status().isNotFound());
    }


    @Test
    void avviaIscrizioneHackathon_notLeader_forbidden() throws Exception {
        Team team = new Team("TeamA");
        MembroTeam leader = new MembroTeam(utente(LEADER), team, RuoloTeam.LEADER);
        team.setLeader(leader);
        MembroTeam member = new MembroTeam(utente(MEMBER), team, RuoloTeam.MEMBRO);
        team.aggiungiMembro(member);
        repositoryTeam.saveAndFlush(team);


        Hackathon hackathon = creaHackathonValido();
        repositoryHackathon.saveAndFlush(hackathon);


        mockMvc.perform(post(ENDPOINT + "/%s/iscrizioni".formatted(hackathon.getNome()))
                        .with(authentication(autenticazione(MEMBER))))
                .andExpect(status().isForbidden());
    }


    @Test
    void avviaIscrizioneHackathon_hackathonNotFound_notFound() throws Exception {
        Team team = new Team("TeamA");
        MembroTeam leader = new MembroTeam(utente(LEADER), team, RuoloTeam.LEADER);
        team.setLeader(leader);
        repositoryTeam.saveAndFlush(team);


        mockMvc.perform(post(ENDPOINT + "/%s/iscrizioni".formatted("noHack"))
                        .with(authentication(autenticazione(LEADER))))
                .andExpect(status().isNotFound());
    }


    @Test
    void avviaIscrizioneHackathon_teamSizeConflict_conflict() throws Exception {
        Team team = new Team("SmallTeam");
        // team with 1 member, less than minimum
        MembroTeam leader = new MembroTeam(utente(LEADER), team, RuoloTeam.LEADER);
        team.setLeader(leader);
        repositoryTeam.saveAndFlush(team);


        Hackathon hackathon = new Hackathon(
                HACK_NAME,
                new Periodo(LocalDate.now().plusDays(10), LocalDate.now().plusDays(12)),
                new BigDecimal("1000"),
                "Luogo",
                5,
                3,
                LocalDateTime.now().plusDays(9),
                "Regolamento",
                10
        );
        repositoryHackathon.saveAndFlush(hackathon);


        mockMvc.perform(post(ENDPOINT + "/%s/iscrizioni".formatted(hackathon.getNome()))
                        .with(authentication(autenticazione(LEADER))))
                .andExpect(status().isConflict());
    }


    @Test
    void avviaIscrizioneHackathon_alreadyRegistered_conflict() throws Exception {
        Team team = new Team("TeamA");
        MembroTeam leader = new MembroTeam(utente(LEADER), team, RuoloTeam.LEADER);
        team.setLeader(leader);
        repositoryTeam.saveAndFlush(team);


        Hackathon hackathon = creaHackathonValido();
        repositoryHackathon.saveAndFlush(hackathon);
        IscrizioneTeam iscr = new IscrizioneTeam(team, hackathon);
        hackathon.aggiungiIscrizione(iscr);
        repositoryHackathon.saveAndFlush(hackathon);


        mockMvc.perform(post(ENDPOINT + "/%s/iscrizioni".formatted(hackathon.getNome()))
                        .with(authentication(autenticazione(LEADER))))
                .andExpect(status().isConflict());
    }


    @Test
    void avviaIscrizioneHackathon_maxIscrizioniReached_conflict() throws Exception {
        Team team = new Team("TeamA");
        MembroTeam leader = new MembroTeam(utente(LEADER), team, RuoloTeam.LEADER);
        team.setLeader(leader);
        repositoryTeam.saveAndFlush(team);


        Hackathon hackathon = creaHackathonValido();
        // fill hackathon with maxIscrizioni entries
        repositoryHackathon.saveAndFlush(hackathon);
        for (int i = 0; i < hackathon.getMaxIscrizioni(); i++) {
            Team t = new Team("T" + i);
            repositoryTeam.saveAndFlush(t);
            IscrizioneTeam iscr = new IscrizioneTeam(t, hackathon);
            hackathon.aggiungiIscrizione(iscr);
        }
        repositoryHackathon.saveAndFlush(hackathon);


        mockMvc.perform(post(ENDPOINT + "/%s/iscrizioni".formatted(hackathon.getNome()))
                        .with(authentication(autenticazione(LEADER))))
                .andExpect(status().isConflict());
    }


    @Test
    void annullaIscrizioneHackathon_ok() throws Exception {
        Team team = new Team("TeamA");
        MembroTeam leader = new MembroTeam(utente(LEADER), team, RuoloTeam.LEADER);
        team.setLeader(leader);
        repositoryTeam.saveAndFlush(team);


        Hackathon hackathon = creaHackathonValido();
        IscrizioneTeam iscr = new IscrizioneTeam(team, hackathon);
        hackathon.aggiungiIscrizione(iscr);
        repositoryHackathon.saveAndFlush(hackathon);


        mockMvc.perform(delete(ENDPOINT + "/%s/iscrizioni/mia".formatted(hackathon.getNome()))
                        .with(authentication(autenticazione(LEADER))))
                .andExpect(status().isNoContent());


        Hackathon persisted = repositoryHackathon.findByNome(hackathon.getNome()).orElseThrow();
        assertTrue(persisted.getIscrizioni().isEmpty());
    }


    @Test
    void annullaIscrizioneHackathon_hackathonInCorso_conflict() throws Exception {
        Team team = new Team("TeamA");
        MembroTeam leader = new MembroTeam(utente(LEADER), team, RuoloTeam.LEADER);
        team.setLeader(leader);
        repositoryTeam.saveAndFlush(team);


        Hackathon hackathon = creaHackathonValido();
        hackathon.setStato(InCorso.INSTANCE);
        hackathon.setStatoEnum(InCorso.INSTANCE);
        repositoryHackathon.saveAndFlush(hackathon);


        mockMvc.perform(delete(ENDPOINT + "/%s/iscrizioni/mia".formatted(hackathon.getNome()))
                        .with(authentication(autenticazione(LEADER))))
                .andExpect(status().isConflict());
    }


    @Test
    void annullaIscrizioneHackathon_notLeader_forbidden() throws Exception {
        Team team = new Team("TeamA");
        MembroTeam leader = new MembroTeam(utente(LEADER), team, RuoloTeam.LEADER);
        team.setLeader(leader);
        MembroTeam member = new MembroTeam(utente(MEMBER), team, RuoloTeam.MEMBRO);
        team.aggiungiMembro(member);
        repositoryTeam.saveAndFlush(team);


        Hackathon hackathon = creaHackathonValido();
        IscrizioneTeam iscr = new IscrizioneTeam(team, hackathon);
        hackathon.aggiungiIscrizione(iscr);
        repositoryHackathon.saveAndFlush(hackathon);


        mockMvc.perform(delete(ENDPOINT + "/%s/iscrizioni/mia".formatted(hackathon.getNome()))
                        .with(authentication(autenticazione(MEMBER))))
                .andExpect(status().isForbidden());
    }


    @Test
    void annullaIscrizioneHackathon_hackathonNotFound_notFound() throws Exception {
        mockMvc.perform(delete(ENDPOINT + "/%s/iscrizioni/mia".formatted("noHack"))
                        .with(authentication(autenticazione(LEADER))))
                .andExpect(status().isNotFound());
    }


    private UsernamePasswordAuthenticationToken autenticazione(String nomeUtente) {
        return new UsernamePasswordAuthenticationToken(
                nomeUtente,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }


    private Utente creaUtente(String nomeUtente){
        return new Utente(nomeUtente, nomeUtente + "@example.com", "pwd");
    }


    private Utente utente(String nomeUtente){
        return repositoryUtente.findByNomeUtente(nomeUtente).orElseThrow(() -> new AssertionError("Utente non trovato: " + nomeUtente));
    }


    private Hackathon creaHackathonValido(){
        return new Hackathon(
                HACK_NAME,
                new Periodo(LocalDate.now().plusDays(10), LocalDate.now().plusDays(12)),
                new BigDecimal("1000"),
                "Luogo",
                5,
                3,
                LocalDateTime.now().plusDays(9),
                "Regolamento",
                10
        );
    }
}

