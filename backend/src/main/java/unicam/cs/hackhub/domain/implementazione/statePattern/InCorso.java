package unicam.cs.hackhub.domain.implementazione.statePattern;

import unicam.cs.hackhub.domain.implementazione.Hackathon;

public class InCorso implements StatoHackathon {
    public static final InCorso INSTANCE = new InCorso();

    private InCorso() {
    }

    @Override
    public void avviaValutazione(Hackathon hackathon) {
        hackathon.setStato(ValutazioneInCorso.INSTANCE);
    }

    @Override
    public void verificaInvioSottomissioneConsentito(Hackathon hackathon) {
    }

    @Override
    public void verificaPropostaDiCallConsentita(Hackathon hackathon) {
    }

    @Override
    public void verificaEspulsioneTeamConsentita(Hackathon hackathon) {
    }
}
