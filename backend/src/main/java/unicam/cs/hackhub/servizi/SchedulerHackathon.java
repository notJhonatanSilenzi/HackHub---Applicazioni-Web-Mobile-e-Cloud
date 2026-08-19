package unicam.cs.hackhub.servizi;

import unicam.cs.hackhub.handler.EventiTemporaliHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulerHackathon {
    private final EventiTemporaliHandler eventiTemporaliHandler;

    public SchedulerHackathon(EventiTemporaliHandler eventiTemporaliHandler) {
        this.eventiTemporaliHandler = eventiTemporaliHandler;
    }

    @Scheduled(fixedRate = 60000)
    public void eseguiControlloScadenze() {
        eventiTemporaliHandler.gestisciScadenzeTemporali();
    }
}
