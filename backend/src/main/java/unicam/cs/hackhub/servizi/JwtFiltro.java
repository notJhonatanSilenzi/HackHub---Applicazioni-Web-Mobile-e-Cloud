package unicam.cs.hackhub.servizi;

import unicam.cs.hackhub.domain.implementazione.Utente;
import unicam.cs.hackhub.repository.RepositoryUtente;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFiltro extends OncePerRequestFilter {

    private final ServizioJwt servizioJwt;
    private final RepositoryUtente repositoryUtente;

    /**
     * Costruttore che inizializza JwtFiltro
     *
     * @param servizioJwt      il servizio jwt per gestire i token
     * @param repositoryUtente il repository degli utenti per prelevare le informazioni dell'utente autenticato
     */
    public JwtFiltro(ServizioJwt servizioJwt, RepositoryUtente repositoryUtente) {
        this.servizioJwt = servizioJwt;
        this.repositoryUtente = repositoryUtente;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        String token;
        String nomeUtente;
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            try {
                nomeUtente = servizioJwt.estraiNomeUtente(token);
                if (nomeUtente != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    Utente utente = repositoryUtente.findByNomeUtente(nomeUtente).orElse(null);
                    if (utente != null) {
                        servizioJwt.validaToken(token, utente);
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(nomeUtente,
                                null, Collections.emptyList());
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            } catch (JwtException | IllegalArgumentException e) {
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }

}
