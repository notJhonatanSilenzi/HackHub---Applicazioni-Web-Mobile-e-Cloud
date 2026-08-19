package unicam.cs.hackhub.testHttp;

import unicam.cs.hackhub.domain.implementazione.Utente;
import unicam.cs.hackhub.repository.RepositoryUtente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EffettuaAutenticazioneBoundaryIT extends BaseHttpIT {

    private static final String ENDPOINT_REGISTRAZIONE = "/api/autenticazione/registrazione";
    private static final String ENDPOINT_ACCESSO = "/api/autenticazione/accesso";

    private static final String NOME_UTENTE = "francesca";
    private static final String EMAIL = "francesca@example.com";
    private static final String PASSWORD = "Password123!";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RepositoryUtente repositoryUtente;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        repositoryUtente.deleteAllInBatch();
        repositoryUtente.flush();
    }

    @Test
    void attivaRegistrazione_created() throws Exception {
        String body = """
                {
                  "nomeUtente": "francesca",
                  "email": "francesca@example.com",
                  "password": "Password123!"
                }
                """;

        mockMvc.perform(post(ENDPOINT_REGISTRAZIONE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));

        Utente utente = repositoryUtente.findByNomeUtente(NOME_UTENTE)
                .orElseThrow(() -> new AssertionError("Utente non trovato nel database"));

        assertAll(
                () -> assertEquals(NOME_UTENTE, utente.getNomeUtente()),
                () -> assertEquals(EMAIL, utente.getEmail()),
                () -> assertNotEquals(PASSWORD, utente.getPasswordHash(),
                        "La password non deve essere salvata in chiaro"),
                () -> assertTrue(passwordEncoder.matches(PASSWORD, utente.getPasswordHash()),
                        "La password hashata dovrebbe corrispondere a quella originale")
        );
    }

    @Test
    void attivaAutenticazione_ok() throws Exception {
        repositoryUtente.saveAndFlush(
                new Utente(
                        NOME_UTENTE,
                        EMAIL,
                        passwordEncoder.encode(PASSWORD)
                )
        );

        String body = """
                {
                  "nomeUtente": "francesca",
                  "password": "Password123!"
                }
                """;

        mockMvc.perform(post(ENDPOINT_ACCESSO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void attivaAutenticazione_nomeUtenteErrato_badRequest() throws Exception {
        String body = """
                {
                  "nomeUtente": "utente_inesistente",
                  "password": "Password123!"
                }
                """;

        mockMvc.perform(post(ENDPOINT_ACCESSO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Nome utente errato"));
    }

    @Test
    void attivaAutenticazione_passwordErrata_badRequest() throws Exception {
        repositoryUtente.saveAndFlush(
                new Utente(
                        NOME_UTENTE,
                        EMAIL,
                        passwordEncoder.encode(PASSWORD)
                )
        );

        String body = """
                {
                  "nomeUtente": "francesca",
                  "password": "password_sbagliata"
                }
                """;

        mockMvc.perform(post(ENDPOINT_ACCESSO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Password errata"));
    }
}