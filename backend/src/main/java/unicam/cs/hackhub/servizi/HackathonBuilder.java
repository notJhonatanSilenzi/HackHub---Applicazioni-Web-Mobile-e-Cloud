package unicam.cs.hackhub.servizi;

import unicam.cs.hackhub.domain.implementazione.Hackathon;
import unicam.cs.hackhub.domain.implementazione.Periodo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HackathonBuilder implements Builder{

    String nome;
    Periodo periodo;
    BigDecimal premio;
    String luogo;
    int teamMax;
    int teamMin;
    String regolamento;
    LocalDateTime scadenzaIscrizioni;
    int maxIscrizioni;

    public void impostaNome(String nome) {
        this.nome = nome;
    }

    public void impostaPeriodo(Periodo periodo) {
        this.periodo = periodo;
    }

    public void impostaPremio(BigDecimal premio) {
        this.premio = premio;
    }

    public void impostaLuogo(String luogo) {
        this.luogo = luogo;
    }

    public void impostaTeamMax(int teamMax) {
        this.teamMax = teamMax;
    }

    public void impostaTeamMin(int teamMin) {
        this.teamMin = teamMin;
    }

    public void impostaRegolamento(String regolamento) {
        this.regolamento = regolamento;
    }

    public void impostaScadenzaIscrizioni(LocalDateTime scadenza) {
        this.scadenzaIscrizioni = scadenza;
    }

    public void impostaMaxIscrizioni(int maxIscrizioni) {
        this.maxIscrizioni = maxIscrizioni;
    }

    public Hackathon getRisultato() {
        return new Hackathon(this.nome, this.periodo, this.premio, this.luogo, this.teamMax, this.teamMin,
                this.scadenzaIscrizioni, this.regolamento, this.maxIscrizioni);
    }

    public void reset() {
        this.nome = null;
        this.periodo = null;
        this.premio = null;
        this.luogo = null;
        this.teamMax = 0;
        this.teamMin = 0;
        this.regolamento = null;
        this.scadenzaIscrizioni = null;
        this.maxIscrizioni = 0;
    }

    public int getMaxIscrizioni() {
        return maxIscrizioni;
    }

    public LocalDateTime getScadenzaIscrizioni() {
        return scadenzaIscrizioni;
    }

    public String getRegolamento() {
        return regolamento;
    }

    public int getTeamMin() {
        return teamMin;
    }

    public int getTeamMax() {
        return teamMax;
    }

    public String getLuogo() {
        return luogo;
    }

    public BigDecimal getPremio() {
        return premio;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public String getNome() {
        return nome;
    }
}
