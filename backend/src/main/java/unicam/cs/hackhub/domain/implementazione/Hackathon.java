package unicam.cs.hackhub.domain.implementazione;

import unicam.cs.hackhub.domain.RuoloStaff;
import unicam.cs.hackhub.domain.StatoEnum;
import unicam.cs.hackhub.domain.implementazione.statePattern.*;
import unicam.cs.hackhub.domain.implementazione.statePattern.*;
import unicam.cs.hackhub.eccezioni.ConflictException;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Classe che gestisce un'hackathon e tutti i suoi elementi
 */
@Entity
@Table(name = "hackathon", uniqueConstraints = @UniqueConstraint(columnNames = "nome"))
public class Hackathon {

    @Id
    private String idHackathon;

    @NotBlank
    private String nome;

    @Embedded
    @Valid
    @NotNull
    private Periodo periodo;

    @NotNull
    private BigDecimal premio;

    @NotBlank
    private String luogo;

    @Max(6)
    private int teamMax;

    @Min(3)
    private int teamMin;

    @Lob
    @NotBlank
    private String regolamento;

    @Min(1)
    private int maxIscrizioni; //Massimo numero di iscrizioni (team) che possono partecipare all'hackathon

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatoEnum statoEnum;

    @Transient
    private StatoHackathon stato;

    @NotNull
    private LocalDateTime scadenzaIscrizioni;

    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Staff> staff;

    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IscrizioneTeam> iscrizioni;

    public Hackathon() {
        staff = new ArrayList<>();
        iscrizioni = new ArrayList<>();
    }

    /**
     * Creazione di un hackathon con tutti i suoi elementi, con valori di default per scadenza iscrizioni e stato iniziale
     *
     * @param nome               nome dell'hackathon, deve essere univoco
     * @param periodo            periodo di svolgimento dell'hackathon
     * @param premio             premio in denaro per il team vincitore, deve essere positivo
     * @param luogo              luogo in cui si svolge l'hackathon
     * @param teamMax            numero massimo di team che possono partecipare all'hackathon, deve essere positivo
     * @param teamMin            numero minimo di team che devono partecipare all'hackathon, deve essere positivo e minore o uguale a teamMax
     * @param regolamento        il regolamento associato all'hackathon
     * @param scadenzaIscrizioni data e ora di scadenza per le iscrizioni all'hackathon, deve essere una data valida e futura
     */
    public Hackathon(String nome, Periodo periodo, BigDecimal premio, String luogo, int teamMax, int teamMin,
                     LocalDateTime scadenzaIscrizioni, String regolamento, int maxIscrizioni) {
        validazione(nome, periodo, premio, luogo, teamMax, teamMin, regolamento, scadenzaIscrizioni);
        this.nome = nome;
        this.periodo = periodo;
        this.premio = premio;
        this.luogo = luogo;
        this.teamMax = teamMax;
        this.teamMin = teamMin;
        this.regolamento = regolamento;
        this.scadenzaIscrizioni = scadenzaIscrizioni;
        this.maxIscrizioni = maxIscrizioni;
        // valori di default / inizializzazioni
        this.stato = IscrizioniAperte.INSTANCE; // stato iniziale, ad esempio "Iscrizioni Aperte"
        setStatoEnum(IscrizioniAperte.INSTANCE);
        this.staff = new ArrayList<>();
        this.iscrizioni = new ArrayList<>();
    }

    /**
     * Assegna un id univoco ad un hackathon
     */
    @PrePersist
    private void assegnaId() {
        if (this.idHackathon == null) {
            this.idHackathon = "H-" + UUID.randomUUID();
        }
    }

    /**
     * Lancia eccezioni se ci sono dei parametri sbagliati dell'hackathon
     *
     * @param nome               il nome
     * @param periodo            il periodo di svolgimento
     * @param premio             il premio
     * @param luogo              il luogo dove si svolge
     * @param teamMax            il numero massimo dei membri che un team deve avere per iscriversi
     * @param teamMin            il numero minimo di membri che un team deve avere per iscriversi
     * @param regolamento        il regolamente
     * @param scadenzaIscrizioni la scadenza delle iscrizioni
     * @throws ConflictException    se alcuni dati non sono validi
     * @throws NullPointerException se alcuni dati non sono stati inseriti
     */
    private void validazione(String nome, Periodo periodo, BigDecimal premio, String luogo, int teamMax, int teamMin,
                             String regolamento, LocalDateTime scadenzaIscrizioni) throws ConflictException,
            NullPointerException {
        if (nome == null || periodo == null || premio == null || luogo == null || regolamento == null || scadenzaIscrizioni == null)
            throw new NullPointerException("Non sono ammessi valori nulli");
        if (nome.length() < 3) throw new ConflictException("Il nome deve avere almeno 3 caratteri di lunghezza");
        if (premio.longValue() <= 0) throw new ConflictException("Il premio deve avere valore positivo");
        if (luogo.length() < 3) throw new ConflictException("Il luogo deve avere almeno 3 caratteri");
        if (teamMin < 3) throw new ConflictException("Il numero minimo di membri per team deve essere almeno 3");
        if (teamMax < teamMin)
            throw new ConflictException("Il numero massimo di membri deve essere almeno il numero minimo");
        if (scadenzaIscrizioni.isEqual(LocalDateTime.now()) || scadenzaIscrizioni.isBefore(LocalDateTime.now()))
            throw new ConflictException("Data oppure orario inseriti non validi");
    }

    /**
     * Aggiunge un iscrizione se i parametri di iscrizione sono validi
     *
     * @param iscrizione l'iscrizione
     */
    public void aggiungiIscrizione(IscrizioneTeam iscrizione) {
        if (iscrizioni.size() >= maxIscrizioni) {
            throw new ConflictException("Numero massimo di iscrizioni raggiunto");
        }
        if (statoEnum != StatoEnum.ISCRIZIONI_APERTE) {
            throw new ConflictException("Non è possibile iscrivere un team, le iscrizioni non sono aperte");
        }
        this.iscrizioni.add(iscrizione);
        iscrizione.setHackathon(this);
    }

    public void rimuoviIscrizione(Team team) {
        IscrizioneTeam iscrizione = iscrizioni.stream().filter(i -> i.getTeam().equals(team)).findFirst()
                .orElseThrow(() -> new ConflictException("Il team non è iscritto a questo hackathon"));
        this.iscrizioni.remove(iscrizione);
        iscrizione.setHackathon(null);
    }

    /**
     * Avvia l'hackathon se è presente almeno un giudice e un mentore, altrimenti lancia un'eccezione
     *
     * @throws ConflictException se non è presente un giudice o un mentore
     */
    public void avviaHackathon() {
        Staff giudice = staff.stream().filter(s -> s.getRuolo().equals(RuoloStaff.GIUDICE)).findFirst()
                .orElse(null);
        List<Staff> mentori = staff.stream().filter(s -> s.getRuolo().equals(RuoloStaff.MENTORE)).toList();
        if (giudice == null || mentori.isEmpty()) {
            throw new ConflictException("Non è possibile avviare l'hackathon senza un giudice e almeno un mentore");
        }
        this.stato.avviaHackathon(this);
    }

    /**
     * Chiude le iscrizioni di questo hackathon
     */
    public void chiudiIscrizioni() {
        this.stato.chiudiIscrizioni(this);
    }

    /**
     * Blocca la possibilità di consegnare o modificare sottomissioni in questo hackathon, dando il via
     * alla fase di valutazione delle sottomissioni da parte del giudice
     */
    public void avviaValutazione() {
        this.stato.avviaValutazione(this);
    }

    public void setStato(StatoHackathon stato) {
        this.stato = stato;
        setStatoEnum(stato);
    }

    public void concludi() {
        this.stato.concludiHackathon(this);
    }

    /**
     * Metodo che tiene traccia degli stati dell'hackathon e simula la sua persistenza nel db
     *
     * @param stato lo StatoHackathon corrente
     */
    public void setStatoEnum(StatoHackathon stato) {
        switch (stato.getClass().getSimpleName()) {
            case "IscrizioniAperte" -> this.statoEnum = StatoEnum.ISCRIZIONI_APERTE;
            case "IscrizioniChiuse" -> this.statoEnum = StatoEnum.ISCRIZIONI_CHIUSE;
            case "InCorso" -> this.statoEnum = StatoEnum.IN_CORSO;
            case "ValutazioneInCorso" -> this.statoEnum = StatoEnum.VALUTAZIONE_IN_CORSO;
            case "Concluso" -> this.statoEnum = StatoEnum.CONCLUSO;
        }
    }

    @PostLoad
    private void initStatoFromEnum() {
        if (this.statoEnum == null) {
            this.stato = IscrizioniAperte.INSTANCE;
            return;
        }
        switch (this.statoEnum) {
            case ISCRIZIONI_APERTE -> this.stato = IscrizioniAperte.INSTANCE;
            case ISCRIZIONI_CHIUSE -> this.stato = IscrizioniChiuse.INSTANCE;
            case IN_CORSO -> this.stato = InCorso.INSTANCE;
            case VALUTAZIONE_IN_CORSO -> this.stato = ValutazioneInCorso.INSTANCE;
            case CONCLUSO -> this.stato = Concluso.INSTANCE;
            default -> this.stato = IscrizioniAperte.INSTANCE;
        }
    }


    public int getTeamMax() {
        return teamMax;
    }

    public int getTeamMin() {
        return teamMin;
    }

    public String getInfo() {
        return this.regolamento;
    }

    public StatoHackathon getStato() {
        return this.stato;
    }

    public StatoEnum getStatoEnum() {
        return statoEnum;
    }

    public String getIdHackathon() {
        return this.idHackathon;
    }

    public String getNome() {
        return this.nome;
    }

    public List<Staff> getStaff() {
        return this.staff;
    }

    public void aggiungiStaff(Staff staff) {
        this.staff.add(staff);
        staff.setHackathon(this);
    }

    public Periodo getPeriodo() {
        return this.periodo;
    }

    public List<IscrizioneTeam> getIscrizioni() {
        return this.iscrizioni;
    }

    public BigDecimal getPremio() {
        return this.premio;
    }

    public String getLuogo() {
        return this.luogo;
    }

    public String getRegolamento() {
        return this.regolamento;
    }

    public LocalDateTime getScadenzaIscrizioni() {
        return this.scadenzaIscrizioni;
    }

    public int getMaxIscrizioni() {
        return this.maxIscrizioni;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
