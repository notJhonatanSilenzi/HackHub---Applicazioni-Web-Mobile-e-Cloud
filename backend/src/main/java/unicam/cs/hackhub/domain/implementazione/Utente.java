package unicam.cs.hackhub.domain.implementazione;

import jakarta.persistence.*;

import java.util.UUID;

/**
 * Un generico Utente che può avere ruoli diversi e che utilizza la piattaforma HackHub in base al ruolo
 * corrente, e dunque ai suoi scopi.
 */
@Entity
@Table(name = "utenti")
public class Utente {

    @Id
    @Column(nullable = false, updatable = false)
    private String idUtente;

    @Column(nullable = false, unique = true)
    private String nomeUtente;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column
    private String recapitoBancario;

    /**
     * Crea un Utente, dato un nome, un id e un ruolo.
     *
     * @param nome -> Il nomeUtente inserito dal nuovo Utente
     */
    public Utente(String nome, String email, String passwordHash) {
        this.nomeUtente = nome;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public Utente() {

    }

    @PrePersist
    private void assegnaId() {
        if (this.idUtente == null) {
            this.idUtente = "U-" + UUID.randomUUID();
        }
    }

    public String getNomeUtente() {
        return this.nomeUtente;
    }

    public String getIdUtente() {
        return idUtente;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setNomeUtente(String nomeUtente) {
        this.nomeUtente = nomeUtente;
    }

    public String getRecapitoBancario() {
        return recapitoBancario;
    }

    public void setRecapitoBancario(String recapitoBancario) {
        this.recapitoBancario = recapitoBancario;
    }
}
