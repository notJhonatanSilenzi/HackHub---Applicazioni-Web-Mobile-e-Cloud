package unicam.cs.hackhub.domain;

public enum RuoloTeam {
    /**
     * Membro del team incaricato di gestire l'iscrizione all'hackathon, gestire il proprio team
     * invitando utenti o togliendoli dal team e gestire call e comunicazioni con il mentore per fornire
     * assistenza al team
     */
    LEADER,

    /**
     * Membro del team che può uscire dal team e inviare o modificare la sottomissione per l'hackathon a cui
     * il proprio team è iscritto
     */
    MEMBRO
}
