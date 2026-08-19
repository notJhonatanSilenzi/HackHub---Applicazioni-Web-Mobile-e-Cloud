package unicam.cs.hackhub.domain.implementazione.statePattern;

import unicam.cs.hackhub.domain.implementazione.Hackathon;

public class IscrizioniChiuse implements StatoHackathon {
    public static final IscrizioniChiuse INSTANCE = new IscrizioniChiuse();

    private IscrizioniChiuse() {}

    @Override
    public void avviaHackathon(Hackathon hackathon) {
        hackathon.setStato(InCorso.INSTANCE);
    }

    @Override
    public void verificaEliminazioneConsentita(Hackathon hackathon) {
    }

}